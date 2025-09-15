package com.example.pos_backend.controller;

import com.example.pos_backend.common.ApiResponse;
import com.example.pos_backend.dto.MerchantRequestDTO;
import com.example.pos_backend.dto.MerchantResponseDTO;
import com.example.pos_backend.dto.MerchantUpdateDTO;
import com.example.pos_backend.dto.MerchantLoginResponseDTO;
import com.example.pos_backend.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家管理控制器 - Square风格
 */
@Slf4j
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
@Tag(name = "商家管理", description = "商家注册、认证、管理相关接口")
public class MerchantController {

    private final MerchantService merchantService;

    /**
     * 商家注册 - Square风格 + 自动登录
     */
    @PostMapping("/register")
    @Operation(summary = "商家注册", description = "创建新的商家账户和默认门店，并自动登录返回访问令牌")
    public ResponseEntity<ApiResponse<MerchantLoginResponseDTO>> registerMerchant(
            @Valid @RequestBody MerchantRequestDTO requestDTO,
            HttpServletRequest request) {
        
        log.info("Merchant registration request received for email: {}", requestDTO.getEmail());
        
        // 获取客户端信息
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        MerchantLoginResponseDTO response = merchantService.registerMerchant(requestDTO, ipAddress, userAgent);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "商家注册成功，已自动登录"));
    }

    /**
     * 根据ID获取商家信息
     */
    @GetMapping("/{merchantId}")
    @Operation(summary = "获取商家信息", description = "根据商家ID获取详细信息")
    public ResponseEntity<ApiResponse<MerchantResponseDTO>> getMerchantById(
            @Parameter(description = "商家ID") @PathVariable String merchantId) {
        
        MerchantResponseDTO response = merchantService.getMerchantById(merchantId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "获取商家信息成功"));
    }

    /**
     * 根据邮箱获取商家信息
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "根据邮箱获取商家", description = "根据邮箱地址获取商家信息")
    public ResponseEntity<ApiResponse<MerchantResponseDTO>> getMerchantByEmail(
            @Parameter(description = "商家邮箱") @PathVariable String email) {
        
        MerchantResponseDTO response = merchantService.getMerchantByEmail(email);
        
        return ResponseEntity.ok(ApiResponse.success(response, "获取商家信息成功"));
    }

    /**
     * 获取所有活跃商家
     */
    @GetMapping("/active")
    @Operation(summary = "获取活跃商家列表", description = "获取所有状态为活跃的商家")
    public ResponseEntity<ApiResponse<List<MerchantResponseDTO>>> getAllActiveMerchants() {
        
        List<MerchantResponseDTO> response = merchantService.getAllActiveMerchants();
        
        return ResponseEntity.ok(ApiResponse.success(response, 
                String.format("获取到 %d 个活跃商家", response.size())));
    }

    /**
     * 根据行业获取商家
     */
    @GetMapping("/industry/{industry}")
    @Operation(summary = "根据行业获取商家", description = "获取指定行业的所有商家")
    public ResponseEntity<ApiResponse<List<MerchantResponseDTO>>> getMerchantsByIndustry(
            @Parameter(description = "行业类型") @PathVariable String industry) {
        
        List<MerchantResponseDTO> response = merchantService.getMerchantsByIndustry(industry);
        
        return ResponseEntity.ok(ApiResponse.success(response, 
                String.format("获取到 %d 个 %s 行业的商家", response.size(), industry)));
    }

    /**
     * 更新商家信息
     */
    @PutMapping("/{merchantId}")
    @Operation(summary = "更新商家信息", description = "更新指定商家的基本信息")
    public ResponseEntity<ApiResponse<MerchantResponseDTO>> updateMerchant(
            @Parameter(description = "商家ID") @PathVariable String merchantId,
            @Valid @RequestBody MerchantUpdateDTO updateDTO) {
        
        log.info("Merchant update request received for ID: {}", merchantId);
        
        MerchantResponseDTO response = merchantService.updateMerchant(merchantId, updateDTO);
        
        return ResponseEntity.ok(ApiResponse.success(response, "商家信息更新成功"));
    }

    /**
     * 删除商家（软删除）
     */
    @DeleteMapping("/{merchantId}")
    @Operation(summary = "删除商家", description = "软删除指定商家及其所有门店")
    public ResponseEntity<ApiResponse<Void>> deleteMerchant(
            @Parameter(description = "商家ID") @PathVariable String merchantId,
            @Parameter(description = "操作人ID") @RequestParam(required = false) String deletedBy) {
        
        log.info("Merchant deletion request received for ID: {}", merchantId);
        
        merchantService.deleteMerchant(merchantId, deletedBy);
        
        return ResponseEntity.ok(ApiResponse.success(null, "商家删除成功"));
    }

    /**
     * 搜索商家
     */
    @GetMapping("/search")
    @Operation(summary = "搜索商家", description = "根据关键词搜索商家（企业名称或邮箱）")
    public ResponseEntity<ApiResponse<List<MerchantResponseDTO>>> searchMerchants(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        
        List<MerchantResponseDTO> response = merchantService.searchMerchants(keyword);
        
        return ResponseEntity.ok(ApiResponse.success(response, 
                String.format("搜索到 %d 个相关商家", response.size())));
    }

    /**
     * 获取活跃商家统计数量
     */
    @GetMapping("/stats/count")
    @Operation(summary = "商家统计", description = "获取活跃商家的总数量")
    public ResponseEntity<ApiResponse<Long>> countActiveMerchants() {
        
        Long count = merchantService.countActiveMerchants();
        
        return ResponseEntity.ok(ApiResponse.success(count, "获取商家统计成功"));
    }

    /**
     * 获取各行业商家统计
     */
    @GetMapping("/stats/industry")
    @Operation(summary = "行业统计", description = "获取各行业商家数量统计")
    public ResponseEntity<ApiResponse<List<Object[]>>> getMerchantsStatsByIndustry() {
        
        List<Object[]> stats = merchantService.countMerchantsByIndustry();
        
        return ResponseEntity.ok(ApiResponse.success(stats, "获取行业统计成功"));
    }

    /**
     * 商家登录认证 - OAuth2版本
     */
    @PostMapping("/login")
    @Operation(summary = "商家登录", description = "商家邮箱密码认证，返回访问令牌")
    public ResponseEntity<ApiResponse<MerchantLoginResponseDTO>> loginMerchant(
            @Parameter(description = "登录邮箱") @RequestParam String email,
            @Parameter(description = "登录密码") @RequestParam String password,
            HttpServletRequest request) {
        
        log.info("Merchant login request received for email: {}", email);
        
        // 获取客户端信息
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        MerchantLoginResponseDTO response = merchantService.authenticateMerchant(email, password, ipAddress, userAgent);
        
        return ResponseEntity.ok(ApiResponse.success(response, "商家登录成功"));
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
