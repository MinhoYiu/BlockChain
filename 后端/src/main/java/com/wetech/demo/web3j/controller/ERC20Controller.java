package com.wetech.demo.web3j.controller;

import com.wetech.demo.web3j.service.ERC20Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/erc20")
@RequiredArgsConstructor
public class ERC20Controller {

    private final ERC20Service erc20Service;

    /**
     * 部署 ERC20 合约
     */
    @PostMapping("/deploy")
    public CompletableFuture<ResponseEntity<Map<String, String>>> deployContract() {
        return erc20Service.deployContract()
                .thenApply(address -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("contractAddress", address);
                    response.put("message", "ERC20 contract deployed successfully");
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Failed to deploy contract: " + ex.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
    }

    /**
     * 加载合约
     */
    @PostMapping("/load")
    public ResponseEntity<Map<String, String>> loadContract(@RequestParam String address) {
        try {
            erc20Service.loadContract(address);
            Map<String, String> response = new HashMap<>();
            response.put("message", "ERC20 contract loaded successfully");
            response.put("contractAddress", address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to load contract: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 查询合约基本信息
     */
    @GetMapping("/info")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getContractInfo() {
        CompletableFuture<String> nameFuture = erc20Service.name();
        CompletableFuture<String> symbolFuture = erc20Service.symbol();
        CompletableFuture<BigInteger> decimalsFuture = erc20Service.decimals();
        CompletableFuture<BigInteger> totalSupplyFuture = erc20Service.totalSupply();

        return nameFuture.thenCompose(name ->
                        symbolFuture.thenCompose(symbol ->
                                decimalsFuture.thenCompose(decimals ->
                                        totalSupplyFuture.thenApply(totalSupply -> {
                                            Map<String, Object> response = new HashMap<>();
                                            response.put("name", name);
                                            response.put("symbol", symbol);
                                            response.put("decimals", decimals);
                                            response.put("totalSupply", totalSupply.toString());
                                            response.put("contractAddress", erc20Service.getContractAddress());
                                            return ResponseEntity.ok(response);
                                        }))))
                .exceptionally(ex -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "Failed to get contract info: " + ex.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
    }

    /**
     * 查询余额
     */
    @GetMapping("/balance")
    public CompletableFuture<ResponseEntity<Map<String, String>>> getBalance(@RequestParam String address) {
        return erc20Service.balanceOf(address)
                .thenApply(balance -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("address", address);
                    response.put("balance", balance.toString());
                    response.put("contractAddress", erc20Service.getContractAddress());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Failed to get balance: " + ex.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
    }

    /**
     * 转账
     */
    @PostMapping("/transfer")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transfer(
            @RequestParam String to,
            @RequestParam String value) {
        BigInteger amount = new BigInteger(value);
        return erc20Service.transfer(to, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("blockNumber", receipt.getBlockNumber().toString());
                    response.put("gasUsed", receipt.getGasUsed().toString());
                    response.put("status", receipt.getStatus());
                    response.put("from", erc20Service.getContractAddress());
                    response.put("to", to);
                    response.put("value", value);
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Transfer failed: " + ex.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
    }

    /**
     * 授权
     */
    @PostMapping("/approve")
    public CompletableFuture<ResponseEntity<Map<String, String>>> approve(
            @RequestParam String spender,
            @RequestParam String value) {
        BigInteger amount = new BigInteger(value);
        return erc20Service.approve(spender, amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("spender", spender);
                    response.put("amount", value);
                    response.put("contractAddress", erc20Service.getContractAddress());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Approve failed: " + ex.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
    }

    /**
     * 查询授权额度
     */
    @GetMapping("/allowance")
    public CompletableFuture<ResponseEntity<Map<String, String>>> allowance(
            @RequestParam String owner,
            @RequestParam String spender) {
        return erc20Service.allowance(owner, spender)
                .thenApply(amount -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("owner", owner);
                    response.put("spender", spender);
                    response.put("allowance", amount.toString());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Failed to get allowance: " + ex.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
    }

    /**
     * 从授权账户转账
     */
    @PostMapping("/transfer-from")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transferFrom(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String value) {
        BigInteger transferAmount = new BigInteger(value);
        return erc20Service.transferFrom(from, to, transferAmount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("from", from);
                    response.put("to", to);
                    response.put("value", value);
                    response.put("contractAddress", erc20Service.getContractAddress());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Transfer from failed: " + ex.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
    }

    /**
     * 铸币（需要权限）
     */
    @PostMapping("/mint")
    public CompletableFuture<ResponseEntity<Map<String, String>>> mint(@RequestParam String value) {
        BigInteger amount = new BigInteger(value);
        return erc20Service.mint(amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("mintedAmount", value);
                    response.put("contractAddress", erc20Service.getContractAddress());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Mint failed: " + ex.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
    }

    /**
     * 销毁代币
     */
    @PostMapping("/burn")
    public CompletableFuture<ResponseEntity<Map<String, String>>> burn(@RequestParam String value) {
        BigInteger amount = new BigInteger(value);
        return erc20Service.burn(amount)
                .thenApply(receipt -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("transactionHash", receipt.getTransactionHash());
                    response.put("burnedAmount", value);
                    response.put("contractAddress", erc20Service.getContractAddress());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Burn failed: " + ex.getMessage());
                    return ResponseEntity.badRequest().body(response);
                });
    }

    /**
     * 获取当前合约地址
     */
    @GetMapping("/address")
    public ResponseEntity<Map<String, String>> getContractAddress() {
        String address = erc20Service.getContractAddress();
        Map<String, String> response = new HashMap<>();
        if (address != null) {
            response.put("contractAddress", address);
        } else {
            response.put("message", "No contract loaded");
        }
        return ResponseEntity.ok(response);
    }
}