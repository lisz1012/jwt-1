package com.lisz;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * @author yueyi2019
 */
public class JwtUtil {
    /**
     * 密钥，仅服务端存储, 重要，不能丢。hash(base64(头 + 体) + 密钥) = 签名
     */
    private static String secret = "ko346134h_we]rg3in_yip1!";

    /**
     *
     * @param subject
     * @param issueDate 签发时间
     * @return
     */
    public static String createToken(String subject, Date issueDate) {
    	
    	
        Calendar c = Calendar.getInstance();  
        c.setTime(issueDate);  
        c.add(Calendar.DAY_OF_MONTH, 20);        
        
    	
    	
        String compactJws = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(issueDate)
                .setExpiration(c.getTime())
                		
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, secret)
                .compact();
        return compactJws;

    }

    /**
     * 解密 jwt
     * @param token
     * @return
     * @throws Exception
     */
    public static String parseToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            if (claims != null){
                System.out.println(Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getSignature());
                return claims.getSubject();
            }
        }catch (ExpiredJwtException e){
            e.printStackTrace();
            System.out.println("jwt过期了");
        }

        return "";
    }

    public static void main(String[] args) {
        String token = createToken("userId=1,role=admin,price=398", new Date());
        System.out.println(token);
        // eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VySWQ9MSxyb2xlPWFkbWluLHByaWNlPTM5OCIsImlhdCI6MTYxNTAwMDU0NSwiZXhwIjoxNjE2NzI0OTQ1fQ.prQOYB7cY7Otyz9K1bvtLDA_CGhl5CNTa3yZL8cq54Lqn0uNEVZccHz_mGRIsq8W3h5NxLwELsweJwBeU8Wcmw
        // xx.xx.xxx
        String strs[] = token.split("\\.");
        System.out.println(new String(Base64.getDecoder().decode(strs[0])));
        System.out.println(new String(Base64.getDecoder().decode(strs[1])));
        parseToken(token);
        //System.out.println(new String(Base64.getDecoder().decode(strs[2])));
    }

//    public static String parseToken(String token){
//        Jws<Claims> jws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
//        if (jws != null) {
//            System.out.println(jws.getSignature());
//            return jws.getBody().getSubject();
//        }
//        return "";
//    }
}
