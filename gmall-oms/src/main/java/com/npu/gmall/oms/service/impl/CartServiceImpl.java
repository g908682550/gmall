package com.npu.gmall.oms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.npu.gmall.constant.CartConstant;
import com.npu.gmall.oms.component.MemberComponent;
import com.npu.gmall.oms.service.CartService;
import com.npu.gmall.pms.entity.Product;
import com.npu.gmall.pms.entity.SkuStock;
import com.npu.gmall.pms.service.ProductService;
import com.npu.gmall.pms.service.SkuStockService;
import com.npu.gmall.ums.entity.Member;
import com.npu.gmall.vo.cart.Cart;
import com.npu.gmall.vo.cart.CartItem;
import com.npu.gmall.vo.cart.CartResponse;
import com.npu.gmall.vo.cart.UserCartKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Component
public class CartServiceImpl implements CartService {

    @Reference
    SkuStockService skuStockService;

    @Reference
    ProductService productService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberComponent memberComponent;

    @Autowired
    RedissonClient redissonClient;


    /**
     * 添加到购物车
     * @param skuId
     * @param num
     * @param cartKey
     * @param accessToken
     * @return
     */
    public CartResponse addToCart(Long skuId, Integer num, String cartKey, String accessToken) {

        //0、根据accessToken获取用户的id
        Member member = memberComponent.getMemberByAccessToken(accessToken);

        //cartkey不为空说明存在离线购物车，将在线及离线购物车进行合并
        if(member!=null&&!StringUtils.isEmpty(cartKey)){
            mergeCart(cartKey,member.getId());
        }

        //获取到用户真正能使用到的购物车，3种情况，用户登录，用户未登录尚未有离线购物车，用户未登录且有离线购物车
        UserCartKey userCartKey = memberComponent.getCartKey(accessToken, cartKey);

        //具体的添加流程
        CartItem cartItem = addItemToCart(skuId,num,userCartKey.getFinalCartKey());

        CartResponse cartResponse=new CartResponse();
        cartResponse.setCartItem(cartItem);

        if(!StringUtils.isEmpty(userCartKey.getTempCartKey())) cartResponse.setCartKey(userCartKey.getTempCartKey());//如果有生成的key，设置进去
        //也可以把整个购物车数据返回，方便操作
        Cart cart = listCart(cartKey, accessToken).getCart();
        cartResponse.setCart(cart);
        return cartResponse;
    }
    /**
     * @param cartKey 老购物车
     * @param id 用户id
     */
    private void mergeCart(String cartKey, Long id) {
        String oldCartKey=CartConstant.TEMP_CART_KEY_PREFIX+cartKey;
        String userCartKey=CartConstant.USER_CART_KEY_PREFIX+id.toString();

        //获取到老购物车的数据
        RMap<String,String> map = redissonClient.getMap(oldCartKey);
        if(map!=null&&!map.isEmpty())
            map.entrySet().forEach(item->{
                //key就是skuId
                String key = item.getKey();
                if(!key.equalsIgnoreCase(CartConstant.CART_CHECKED_KEY)){
                    //购物项的json数据
                    String value = item.getValue();
                    CartItem cartItem = JSON.parseObject(value, CartItem.class);
                    addItemToCart(Long.parseLong(key),cartItem.getCount(),userCartKey);
                }
            });
        map.clear();
    }

    /**
     * 给指定商品添加到购物车
     * @param skuId
     * @param num
     * @param finalCartKey
     * @return
     */
    private CartItem addItemToCart(Long skuId,Integer num,String finalCartKey) {

        CartItem newCartItem = new CartItem();
        /**
         * 异步运行获取商品信息，可以使用线程池
         */
        CompletableFuture<Void> skuFuture = CompletableFuture.supplyAsync(() -> {
            //查出skuId在数据库对应的最新详情,远程查询
            SkuStock skuStock = skuStockService.getById(skuId);
            return skuStock;
        }).thenAccept(skuStock -> {
            Product product = productService.getById(skuStock.getProductId());
            BeanUtils.copyProperties(skuStock, newCartItem);
            newCartItem.setSkuId(skuStock.getId());
            newCartItem.setName(product.getName());
            newCartItem.setCount(num);
            newCartItem.setCheck(true);
        });

        RMap<String, String> map = redissonClient.getMap(finalCartKey);
        //获取购物车这个skuId对应的购物项
        String itemJson = map.get(skuId.toString());
        //购物项之前是否存在
        if(!StringUtils.isEmpty(itemJson)){
            CartItem oldItem = JSON.parseObject(itemJson, CartItem.class);
            Integer count = oldItem.getCount();
            try {
                skuFuture.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            newCartItem.setCount(count+num);
            String newItemJson = JSON.toJSONString(newCartItem);
            map.put(skuId.toString(),newItemJson);
        }else{
            try {
                skuFuture.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //新增购物项
            String newItemJson = JSON.toJSONString(newCartItem);
            map.put(skuId.toString(),newItemJson);
        }
        //维护勾选状态列表
        checkItem(Arrays.asList(skuId),true,finalCartKey);
        return newCartItem;
    }

    /**
     * 选中或不选中某些购物项
     * @param skuIds
     * @param ops
     * @param cartKey
     * @param accessToken
     * @return
     */
    @Override
    public CartResponse checkCartItems(String skuIds, Integer ops, String cartKey, String accessToken) {

        List<Long> skuIdsList=new ArrayList<>();
        String finalCartKey=memberComponent.getCartKey(accessToken,cartKey).getFinalCartKey();
        RMap<String, String> map = getCartMap(cartKey, accessToken);
        boolean checked=ops==1?true:false;
        //找到每个sku对应购物车的json,把状态check改为ops对应的值

        //修改购物车状态
        if(!StringUtils.isEmpty(skuIds)){
            String[] ids = skuIds.split(",");
            for(String id:ids){
                Long skuId = Long.parseLong(id);
                skuIdsList.add(skuId);
                if(map!=null&&!map.isEmpty()){
                    String jsonItem = map.get(id);
                    CartItem cartItem = JSON.parseObject(jsonItem, CartItem.class);
                    cartItem.setCheck(checked);
                    //覆盖redis原数据
                    map.put(id,JSON.toJSONString(cartItem));
                }
            }
        }
        //修改购物车状态
        checkItem(skuIdsList,checked,finalCartKey);

        //返回整个购物车
        CartResponse cartResponse = listCart(cartKey, accessToken);
        return cartResponse;
    }

    /**
     * 通过redisson获取当前key对应的map
     * @param cartKey
     * @param accessToken
     * @return
     */
    public RMap<String, String>  getCartMap(String cartKey, String accessToken) {
        UserCartKey userCartKey=memberComponent.getCartKey(accessToken,cartKey);
        String finalCartKey = userCartKey.getFinalCartKey();
        RMap<String, String> map = redissonClient.getMap(finalCartKey);
        return map;
    }

    /**
     * 删除购物车的某项
     * @param skuId
     * @param cartKey
     * @param accessToken
     * @return
     */
    @Override
    public CartResponse delCartItem(Long skuId, String cartKey, String accessToken) {

        //维护购物项的状态
        checkItem(Arrays.asList(skuId),false,memberComponent.getCartKey(accessToken,cartKey).getFinalCartKey());
        RMap<String, String> map = getCartMap(cartKey, accessToken);
        map.remove(skuId.toString());
        return listCart(accessToken,cartKey);
    }

    /**
     * 查询购物车所有项
     * @param cartKey
     * @param accessToken
     * @return
     */
    @Override
    public CartResponse listCart(String cartKey, String accessToken) {
        CartResponse cartResponse=new CartResponse();
        Cart cart = new Cart();
        List<CartItem> cartItems=new ArrayList<>();
        UserCartKey userCartKey = memberComponent.getCartKey(accessToken, cartKey);

        //用户登录了，需要合并购物车
        if(userCartKey.getLogin()) mergeCart(cartKey,userCartKey.getUserId());

        String finalCartKey = userCartKey.getFinalCartKey();

        stringRedisTemplate.expire(finalCartKey,1L, TimeUnit.DAYS);

        RMap<String, String> map = redissonClient.getMap(finalCartKey);

        if(map!=null&&!map.isEmpty()){
            map.entrySet().forEach(item->{
                if(!item.getKey().equalsIgnoreCase(CartConstant.CART_CHECKED_KEY)){
                    String skuId = item.getKey();
                    String itemJson = item.getValue();
                    CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);
                    cartItem.setSkuId(Long.parseLong(skuId));
                    cartItems.add(cartItem);
                }
            });
            cart.setCartItems(cartItems);
        }else{
            //如果用户没有购物车，新建一个购物车
            cartResponse.setCartKey(userCartKey.getTempCartKey());
        }
        cartResponse.setCart(cart);
        return cartResponse;
    }

    /**
     * 清空购物车
     * @param cartKey
     * @param accessToken
     * @return
     */
    @Override
    public CartResponse clearCart(String cartKey, String accessToken) {
        UserCartKey userCartKey=memberComponent.getCartKey(accessToken,cartKey);
        RMap<String, String> map = getCartMap(cartKey, accessToken);

        List<Long> skuIds=new ArrayList<>();

        map.entrySet().forEach(item->{
            if(!item.getKey().equalsIgnoreCase(CartConstant.CART_CHECKED_KEY)) skuIds.add(Long.parseLong(item.getKey()));
        });

        checkItem(skuIds,false,userCartKey.getFinalCartKey());

        map.clear();
        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartKey(userCartKey.getTempCartKey());
        return cartResponse;
    }


    /**
     * 添加购物车已有商品的属性
     * @param skuId
     * @param num
     * @param cartKey
     * @param accessToken
     * @return
     */
    @Override
    public CartResponse updateCartItem(Long skuId, Integer num, String cartKey, String accessToken) {
        RMap<String, String> map = getCartMap(cartKey, accessToken);

        String itemJson = map.get(skuId.toString());

        CartItem cartItem = JSON.parseObject(itemJson, CartItem.class);

        cartItem.setCount(num);

        String newItemJson = JSON.toJSONString(cartItem);

        map.put(skuId.toString(),newItemJson);

        CartResponse cartResponse = new CartResponse();

        cartResponse.setCartItem(cartItem);

        return cartResponse;
    }

    /**
     * 维护购物车状态
     * @param skuIdsList
     * @param checked
     * @param finalCartKey
     */
    private void checkItem(List<Long> skuIdsList,Boolean checked,String finalCartKey){

        RMap<String, String> map = redissonClient.getMap(finalCartKey);

        //修改checked集合的状态
        String checkedJson = map.get(CartConstant.CART_CHECKED_KEY);
        //2、为了快速找到哪个被选中了，单独维护一个check数组 数组在map中用的key是checked，checked值是Set集合最好
        Set<Long> longSet=JSON.parseObject(checkedJson,new TypeReference<Set<Long>>(){});
        if(longSet==null||longSet.isEmpty()) longSet=new HashSet<>();
        if(checked){
            //重新保存被选中的商品
            longSet.addAll(skuIdsList);
        }else{
            longSet.removeAll(skuIdsList);
        }
        map.put(CartConstant.CART_CHECKED_KEY,JSON.toJSONString(longSet));
    }

    /**
     * 订单服务调用，清除购物车中已经下单的商品
     * @param accessToken
     * @param skuIds
     */
    @Override
    public void removeCartItem(String accessToken, List<Long> skuIds) {
        RMap<String, String> cartMap = getCartMap(null, accessToken);
        skuIds.forEach(id->{
            //移除商品项
            cartMap.remove(id.toString());
        });
        //移除勾选状态的保存
        cartMap.put(CartConstant.CART_CHECKED_KEY,JSON.toJSONString(new LinkedHashSet<>()));
    }

    /**
     * 订单服务调用，根据用户信息获取购物车内所选商品总条目
     * @param accessToken
     * @return
     */
    @Override
    public List<CartItem> getCartItemForOrder(String accessToken) {
        //获取用户中被选中的数据
        List<CartItem> cartItems=new ArrayList<>();

        UserCartKey cartKey = memberComponent.getCartKey(accessToken, null);
        RMap<String, String> map = redissonClient.getMap(cartKey.getFinalCartKey());
        String checkItemsJson= map.get(CartConstant.CART_CHECKED_KEY);
        Set<Long> items = JSON.parseObject(checkItemsJson, new TypeReference<Set<Long>>(){});

        items.forEach(item->{
            String itemJson = map.get(item.toString());
            cartItems.add(JSON.parseObject(itemJson,CartItem.class));
        });
        return cartItems;
    }

}
