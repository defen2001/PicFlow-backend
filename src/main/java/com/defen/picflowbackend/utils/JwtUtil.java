package com.defen.picflowbackend.utils;

import com.defen.picflowbackend.exception.BusinessException;
import com.defen.picflowbackend.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    // 使用 JJWT 0.12 的 Keys 工具生成密钥，实际生产环境中建议将密钥配置化或放入环境变量中
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    // Token 有效期 1 小时（单位：毫秒）
    private static final long EXPIRATION_TIME = 3600_000;

    /**
     * 生成 JWT Token
     *
     * @param userAccount 用户名作为 token 的主题
     * @return 生成的 token 字符串
     */
    public static String generateToken(String userId, String userAccount) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(userId)
                .claim("userAccount", userAccount)
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(SECRET_KEY)  // 自动使用 HS256 算法签名
                .compact();
    }

    /**
     * 解析 JWT Token 获取 Claims
     *
     * @param token JWT Token
     * @return 解析后的 Claims 对象
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 JWT Token 是否有效
     *
     * @param token JWT Token
     * @return 如果 token 合法且未过期返回 true，否则返回 false
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            // 解析异常或 token 过期时均返回 false
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户登录过期");
        }
    }

}
