package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.erc20dl202330550291.ERC20Dl202330550291;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ERC20Service {

    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;

    private ERC20Dl202330550291 contract;

    @Getter
    private String contractAddress;

    /**
     * 部署 ERC20 代币合约
     */
    public CompletableFuture<String> deployContract() {
        log.info("Deploying ERC20Dl202330550291 contract...");
        return ERC20Dl202330550291.deploy(web3j, credentials, gasProvider)
                .sendAsync()
                .thenApply(deployedContract -> {
                    this.contract = deployedContract;
                    this.contractAddress = deployedContract.getContractAddress();
                    log.info("ERC20 contract deployed to: {}", contractAddress);
                    return contractAddress;
                });
    }

    /**
     * 加载已部署的合约
     */
    public void loadContract(String contractAddress) {
        log.info("Loading ERC20 contract from address: {}", contractAddress);
        this.contract = ERC20Dl202330550291.load(contractAddress, web3j, credentials, gasProvider);
        this.contractAddress = contractAddress;
    }

    /**
     * 查询代币名称
     */
    public CompletableFuture<String> name() {
        validateContract();
        return contract.name().sendAsync();
    }

    /**
     * 查询代币符号
     */
    public CompletableFuture<String> symbol() {
        validateContract();
        return contract.symbol().sendAsync();
    }

    /**
     * 查询小数位数
     */
    public CompletableFuture<BigInteger> decimals() {
        validateContract();
        return contract.decimals().sendAsync();
    }

    /**
     * 查询总供应量
     */
    public CompletableFuture<BigInteger> totalSupply() {
        validateContract();
        return contract.totalSupply().sendAsync();
    }

    /**
     * 查询账户余额
     */
    public CompletableFuture<BigInteger> balanceOf(String address) {
        validateContract();
        log.info("Querying balance for address: {}", address);
        return contract.balanceOf(address).sendAsync();
    }

    /**
     * 转账
     */
    public CompletableFuture<TransactionReceipt> transfer(String to, BigInteger value) {
        validateContract();
        log.info("Transferring {} tokens to {}", value, to);
        return contract.transfer(to, value).sendAsync();
    }

    /**
     * 授权额度
     */
    public CompletableFuture<TransactionReceipt> approve(String spender, BigInteger value) {
        validateContract();
        log.info("Approving {} tokens for spender {}", value, spender);
        return contract.approve(spender, value).sendAsync();
    }

    /**
     * 查询授权额度
     */
    public CompletableFuture<BigInteger> allowance(String owner, String spender) {
        validateContract();
        log.info("Querying allowance from {} to {}", owner, spender);
        return contract.allowance(owner, spender).sendAsync();
    }

    /**
     * 从授权账户转账
     */
    public CompletableFuture<TransactionReceipt> transferFrom(String from, String to, BigInteger value) {
        validateContract();
        log.info("Transferring {} tokens from {} to {}", value, from, to);
        return contract.transferFrom(from, to, value).sendAsync();
    }

    /**
     * 铸币（需要合约权限）
     */
    public CompletableFuture<TransactionReceipt> mint(BigInteger value) {
        validateContract();
        log.info("Minting {} new tokens", value);
        return contract.mint(value).sendAsync();
    }

    /**
     * 销毁代币
     */
    public CompletableFuture<TransactionReceipt> burn(BigInteger value) {
        validateContract();
        log.info("Burning {} tokens", value);
        return contract.burn(value).sendAsync();
    }

    private void validateContract() {
        if (contract == null) {
            throw new IllegalStateException("ERC20 contract not deployed or loaded");
        }
    }
}