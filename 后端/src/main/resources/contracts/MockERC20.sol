// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.20;

contract ERC20Dl202330550291 {
    // =========================================================================
// 【1】代币数据核心：状态变量
// =========================================================================

// _balances: 存储每个地址拥有的代币数量 (地址 => 余额)
    mapping(address => uint256) private _balances;

// _allowances: 存储授权额度 (所有者 => 授权使用方 => 额度)
    mapping(address => mapping(address => uint256)) private _allowances;

// _totalSupply: 代币总发行量
    uint256 private _totalSupply;

// 代币元数据
    string private _name = "ERC20Dl202330550291";
    string private _symbol = "ERC20Dl";
    uint8 private constant _decimals = 18; // 代币精度，默认 18 位

// =========================================================================
// 【2】ERC20 标准事件
// =========================================================================

// Transfer 事件：在代币发行、销毁或转移时必须触发
    event Transfer(address indexed _from, address indexed _to, uint256 _value);

// Approval 事件：在授权操作时必须触发
    event Approval(address indexed _owner, address indexed _spender, uint256 _value);

// =========================================================================
// 【3】合约主体：DLToken
// =========================================================================
    constructor() {}

    // name(): 返回代币的名称
    function name() public view returns (string memory) {
        return _name;
    }

    // symbol(): 返回代币的缩写符号
    function symbol() public view returns (string memory) {
        return _symbol;
    }

    // decimals(): 返回代币精度位数
    function decimals() public pure returns (uint8) {
        return _decimals;
    }

    // totalSupply(): 返回代币总发行数量
    function totalSupply() public view returns (uint256) {
        return _totalSupply;
    }

    // balanceOf(address _owner): 查询某个地址下的代币数量
    function balanceOf(address _owner) public view returns (uint256 balance) {
        return _balances[_owner];
    }

    // allowance(address _owner, address _spender): 查询授权额度
    function allowance(address _owner, address _spender) public view returns (uint256 remaining) {
        return _allowances[_owner][_spender];
    }

    // ---------------------------------------------------------------------
    // ERC20 核心交易函数 (Core Transaction Functions)
    // ---------------------------------------------------------------------

    // transfer(address _to, uint256 _value): 向某个地址转账代币
    function transfer(address _to, uint256 _value) public returns (bool success) {
        // 检查余额
        require(_balances[msg.sender] >= _value, "ERC20: transfer amount exceeds balance");

        // 扣除调用者余额，增加接收方余额
        _balances[msg.sender] -= _value;
        _balances[_to] += _value;

        // 触发事件
        emit Transfer(msg.sender, _to, _value);
        return true;
    }

    // approve(address _spender, uint256 _value): 授权第三方使用代币
    function approve(address _spender, uint256 _value) public returns (bool success) {
        // 记录授权额度
        _allowances[msg.sender][_spender] = _value;

        // 触发事件
        emit Approval(msg.sender, _spender, _value);
        return true;
    }

    // transferFrom(address _from, address _to, uint256 _value): 从授权地址转账
    function transferFrom(address _from, address _to, uint256 _value) public returns (bool success) {
        // 1. 检查授权额度
        require(_allowances[_from][msg.sender] >= _value, "ERC20: transfer amount exceeds allowance");
        // 2. 检查余额
        require(_balances[_from] >= _value, "ERC20: transfer amount exceeds balance");

        // 3. 扣除授权额度
        _allowances[_from][msg.sender] -= _value;

        // 4. 转移代币
        _balances[_from] -= _value;
        _balances[_to] += _value;

        // 5. 触发事件
        emit Transfer(_from, _to, _value);
        return true;
    }

    // ---------------------------------------------------------------------
    // 实验额外要求的 Mint/Burn 函数
    // ---------------------------------------------------------------------

    // mint(uint256 value): 发行资产（铸币）
    function mint(uint256 value) public {
        // 增加总供应量
        _totalSupply += value;
        // 增加调用者余额
        _balances[msg.sender] += value;

        // 触发 Transfer 事件 (从 0x0 地址到铸币者)
        emit Transfer(address(0), msg.sender, value);
    }

    // burn(uint256 value): 销毁资产
    function burn(uint256 value) public {
        // 检查余额是否足够销毁
        require(_balances[msg.sender] >= value, "ERC20: burn amount exceeds balance");

        // 减少总供应量
        _totalSupply -= value;
        // 减少调用者余额
        _balances[msg.sender] -= value;

        // 触发 Transfer 事件 (从销毁者到 0x0 地址)
        emit Transfer(msg.sender, address(0), value);
    }
}