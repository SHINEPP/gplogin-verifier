package com.chat.googleapi.app.module.apple;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

// https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api
public class AppleLoginUtils {

    private static final String appleAuthUrl = "https://appleid.apple.com/auth/keys";
    private static final String appleIssuerUrl = "https://appleid.apple.com";
    private static final String appleAud = "com.example.flirter.dbg";

    private static final Map<String, Map<String, String>> authKeysMap = new HashMap<>();

    static {
        // https://developer.apple.com/documentation/sign_in_with_apple/jwkset/keys
        try {
            String json = HttpClientUtils.doGet(appleAuthUrl);
            JSONObject auth = JSONObject.parseObject(json);
            JSONArray keys = auth.getJSONArray("keys");
            for (int i = 0; i < keys.size(); i++) {
                JSONObject obj = keys.getJSONObject(i);
                Map<String, String> m = new HashMap<>();
                m.put("kty", obj.getString("kty"));
                m.put("n", obj.getString("n"));
                m.put("e", obj.getString("e"));
                authKeysMap.put(obj.getString("kid"), m);
            }
        } catch (Exception e) {
            System.err.println("parserAuthKeys(), e = " + e);
        }
    }

    /**
     * 对前端传来的identityToken进行验证
     */
    public static boolean verifyToken(String identityToken) {
        try {
            // 1 解析
            Map<String, JSONObject> map = parserIdentityToken(identityToken);
            String kid = map.get("header").getString("kid");
            // 2 生成publicKey
            PublicKey publicKey = getPublicKey(kid);
            if (publicKey == null) {
                System.err.println("verifyToken(), Get PublicKey Error");
                return false;
            }
            // 3 验证
            // https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
            JwtParser jwtParser = Jwts.parser()
                    .requireIssuer(appleIssuerUrl)
                    .requireAudience(appleAud)
                    .verifyWith(publicKey)
                    .build();
            Jws<Claims> claims = jwtParser.parseSignedClaims(identityToken);
            if (claims != null) {
                System.out.println("verifyToken(), claims = " + claims);
                Claims payload = claims.getPayload();
                String sub = payload.get("sub").toString(); //sub,即用户的Apple的openId
                String iss = payload.get("iss").toString();
                String aud = payload.get("aud").toString();
                if (appleIssuerUrl.equals(iss) && aud.contains(aud)) {
                    System.out.println("verifyToken(), verify success, sub = " + sub);
                    return true;
                }
            }
        } catch (ExpiredJwtException e) {
            System.err.println("verifyToken(), Apple idToken expired, e = " + e);
        } catch (Exception e) {
            System.err.println("verifyToken(), Apple idToken illegal, e = " + e);
        }
        return false;
    }

    /**
     * 对前端传来的JWT字符串identityToken的第二部分进行解码
     * 主要获取其中的aud和sub，aud对应ios前端的包名，sub对应当前用户的授权openID
     */
    private static Map<String, JSONObject> parserIdentityToken(String token) {
        Map<String, JSONObject> map = new HashMap<>();
        try {
            String[] arr = token.split("\\.");
            String deHeader = new String(Base64.decodeBase64(arr[0]));
            System.out.println("header=" + deHeader);
            JSONObject header = JSON.parseObject(deHeader);
            map.put("header", header);
            String dePayload = new String(Base64.decodeBase64(arr[1]));
            System.out.println("dePayload=" + dePayload);
            JSONObject payload = JSON.parseObject(dePayload);
            map.put("payload", payload);
        } catch (Exception e) {
            System.err.println("parserIdentityToken(), e = " + e);
        }
        return map;
    }

    /**
     * 获取苹果的公钥
     */
    private static PublicKey getPublicKey(String kid) {
        try {
            Map<String, String> key = authKeysMap.get(kid);
            if (key == null) {
                return null;
            }
            String kty = key.get("kty");
            String n = key.get("n");
            String e = key.get("e");
            BigInteger modulus = new BigInteger(1, Base64.decodeBase64(n));
            BigInteger publicExponent = new BigInteger(1, Base64.decodeBase64(e));
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
            KeyFactory kf = KeyFactory.getInstance(kty);//目前kty均为 "RSA"
            return kf.generatePublic(spec);
        } catch (Exception e) {
            System.err.println("getPublicKey(), e " + e);
        }
        return null;
    }
}
