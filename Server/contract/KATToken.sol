pragma solidity ^0.8.19;
pragma experimental ABIEncoderV2;

interface IERC20 {
    function totalSupply() external view returns (uint256);

    function balanceOf(address account) external view returns (uint256);

    function transfer(address from, address recipient, uint256 amount, string calldata transferType) external returns (bool);

    function allowance(address owner, address spender) external view returns (uint256);

    function approve(address spender, uint256 amount) external returns (bool);

    function transferFrom(address sender, address recipient, uint256 amount) external returns (bool);

    event Transfer(address indexed from, address indexed to, uint256 value);

    event MsgSender(address msgSender);

    event Approval(address indexed owner, address indexed spender, uint256 value);
}

library SafeMath {
    /**
     * @dev 두 부호 없는 정수의 합을 반환합니다.
     * 오버플로우 발생 시 예외처리합니다.
     *
     * 솔리디티의 `+` 연산자를 대체합니다.
     *
     * 요구사항:
     * - 덧셈은 오버플로우될 수 없습니다.
     */
    function add(uint256 a, uint256 b) internal pure returns (uint256) {
        uint256 c = a + b;
        require(c >= a, "SafeMath: addition overflow");

        return c;
    }

    /**
     * @dev 두 부호 없는 정수의 차를 반환합니다.
     * 결과가 음수일 경우 오버플로우입니다.
     *
     * 솔리디티의 `-` 연산자를 대체합니다.
     *
     * 요구사항:
     * - 뺄셈은 오버플로우될 수 없습니다.
     */
    function sub(uint256 a, uint256 b) internal pure returns (uint256) {
        require(b <= a, "SafeMath: subtraction overflow");
        uint256 c = a - b;

        return c;
    }

    /**
     * @dev 두 부호 없는 정수의 곱을 반환합니다.
     * 오버플로우 발생 시 예외처리합니다.
     *
     * 솔리디티의 `*` 연산자를 대체합니다.
     *
     * 요구사항:
     * - 곱셈은 오버플로우될 수 없습니다.
     */
    function mul(uint256 a, uint256 b) internal pure returns (uint256) {
        // 가스 최적화: 이는 'a'가 0이 아님을 요구하는 것보다 저렴하지만,
        // 'b'도 테스트할 경우 이점이 없어집니다.
        // See: https://github.com/OpenZeppelin/openzeppelin-solidity/pull/522
        if (a == 0) {
            return 0;
        }

        uint256 c = a * b;
        require(c / a == b, "SafeMath: multiplication overflow");

        return c;
    }

    /**
     * @dev 두 부호 없는 정수의 몫을 반환합니다. 0으로 나누기를 시도할 경우
     * 예외처리합니다. 결과는 0의 자리에서 반올림됩니다.
     *
     * 솔리디티의 `/` 연산자를 대체합니다. 참고: 이 함수는
     * `revert` 명령코드(잔여 가스를 건들지 않음)를 사용하는 반면, 솔리디티는
     * 유효하지 않은 명령코드를 사용해 복귀합니다(남은 모든 가스를 소비).
     *
     * 요구사항:
     * - 0으로 나눌 수 없습니다.
     */
    function div(uint256 a, uint256 b) internal pure returns (uint256) {
        // 솔리디티는 0으로 나누기를 자동으로 검출하고 중단합니다.
        require(b > 0, "SafeMath: division by zero");
        uint256 c = a / b;
        // assert(a == b * c + a % b); // 이를 만족시키지 않는 경우가 없어야 합니다.

        return c;
    }

    /**
     * @dev 두 부호 없는 정수의 나머지를 반환합니다. (부호 없는 정수 모듈로 연산),
     * 0으로 나눌 경우 예외처리합니다.
     *
     * 솔리디티의 `%` 연산자를 대체합니다. 이 함수는 `revert`
     * 명령코드(잔여 가스를 건들지 않음)를 사용하는 반면, 솔리디티는
     * 유효하지 않은 명령코드를 사용해 복귀합니다(남은 모든 가스를 소비).
     *
     * 요구사항:
     * - 0으로 나눌 수 없습니다.
     */
    function mod(uint256 a, uint256 b) internal pure returns (uint256) {
        require(b != 0, "SafeMath: modulo by zero");
        return a % b;
    }
}

contract KATToken is IERC20 {
    using SafeMath for uint256;

    struct TransferData {
        address sender;
        address recipient;
        uint256 timeStamp;
        uint amount;
        string transferType;
    }
    
    mapping (address => uint256) private _balances;

    mapping (address => mapping (address => uint256)) private _allowances;

    mapping (address => TransferData[]) private transferLog;

    mapping (address => uint256) private transferLogCnt;

    string private _name;
    string private _symbol;
    uint8 private _decimals;

    constructor (string memory name, string memory symbol, uint8 decimals) public {
        _name = name;
        _symbol = symbol;
        _decimals = decimals;

        _mint(msg.sender, 100000 * 10 ** uint(decimals));
    }

    /**
    *   @dev 토큰 이름을 반환한다.
    */
    function name() public view returns (string memory) {
        return _name;
    }

    /**
    *   @dev 주로 이름을 줄여서 표현한 토큰 심볼을
    * 반환합니다.
    **/
    
    function symbol() public view returns (string memory) {
        return _symbol;
    }

        /**
     * @dev 사용자 표현을 위한 소수 자릿수를 반환합니다.
     * 예를 들어, `decimals`이  `2`인 경우, 505` 토큰은
     * 사용자에게 `5,05` (`505 / 10 ** 2`)와 같이 표시되어야 합니다.
     *
     * 토큰은 보통 18의 값을 취하며, 이는 Ether와 Wei의 관계를
     * 모방한 것입니다.
     *
     * > 이 정보는 디스플레이 목적으로만 사용됩니다.
     * `IERC20.balanceOf`와 `IERC20.transfer`를 포함해
     * 컨트랙트의 산술 연산에 어떠한 영향을 주지 않습니다.
     */
    function decimals() public view returns (uint8) {
        return _decimals;
    }
    // https://github.com/OpenZeppelin/openzeppelin-solidity/blob/v2.3.0/contracts/token/ERC20/ERC20Detailed.sol 끝 부분을 참고

    uint256 private _totalSupply;

    /**
     * @dev `IERC20.totalSupply`를 참조하세요.
     */
    function totalSupply() public view returns (uint256) {
        return _totalSupply;
    }

    /**
     * @dev `IERC20.balanceOf`를 참조하세요.
     */
    function balanceOf(address account) public view returns (uint256) {
        return _balances[account];
    }

    /**
     * @dev `IERC20.transfer`를 참조.
     *
     * 요구사항 :
     *
     * - `recipient`는 영 주소(0x0000...0)가 될 수 없습니다.
     * - 호출자의 잔고는 적어도 `amount` 이상이어야 합니다.
     */
    function transfer(address from, address recipient, uint256 amount, string memory transferType) public returns (bool) {
        _transfer(from, recipient, amount, transferType);
        emit MsgSender(msg.sender);
        return true;
    }

    /**
     * @dev `IERC20.allowance`를 참조하세요.
     */
    function allowance(address owner, address spender) public view returns (uint256) {
        return _allowances[owner][spender];
    }

    /**
     * @dev `IERC20.approve`를 참조하세요.
     *
     * 요구사항:
     *
     * - `spender`는 영 주소가 될 수 없습니다.
     */
    function approve(address spender, uint256 value) public returns (bool) {
        _approve(msg.sender, spender, value);
        return true;
    }

   /**
     * @dev `IERC20.transferFrom`를 참조하세요.
     *
     * 업데이트된 허용량을 나타내는 `Approval` 이벤트가 발생합니다. 이것은 EIP에서
     * 요구되는 바가 아닙니다. `ERC20`의 시작 부분에 있는 참고 사항을 참조하세요.
     *
     * 요구사항:
     * - `sender`와 `recipient`는 영 주소가 될 수 없습니다.
     * - `sender`의 잔고는 적어도 `value` 이상이어야 합니다.
     * - 호출자는 `sender`의 토큰에 대해 최소한 `amount` 만큼의 허용량을
     * 가져야 합니다.
     */
    function transferFrom(address sender, address recipient, uint256 amount) public returns (bool) {
        _transfer(sender, recipient, amount, "transferFrom");
        _approve(sender, msg.sender, _allowances[sender][msg.sender].sub(amount));

        emit MsgSender(msg.sender);
        return true;
    }

    /**
     * @dev 호출자에 의해 원자적(atomically)으로 `spender`에 승인된 허용량을 증가시킵니다.
     *
     * 이것은 `IERC20.approve`에 기술된 문제에 대한 완화책으로 사용될 수 있는
     * `approve`의 대안입니다.
     *
     * 업데이트된 허용량을 나타내는 `Approval` 이벤트가 발생합니다.
     *
     * 요구사항:
     *
     * - `spender`는 영 주소가 될 수 없습니다.
     */
    function increaseAllowance(address spender, uint256 addedValue) public returns (bool) {
        _approve(msg.sender, spender, _allowances[msg.sender][spender].add(addedValue));
        return true;
    }

    /**
     * @dev 호출자에 의해 원자적으로 `spender`에 승인된 허용량을 감소시킵니다.
     *
     * 이것은 `IERC20.approve`에 기술된 문제에 대한 완화책으로 사용될 수 있는
     * `approve`의 대안입니다.
     *
     * 업데이트된 허용량을 나타내는 `Approval` 이벤트가 발생합니다.
     *
     * 요구사항:
     *
     * - `spender`는 영 주소가 될 수 없습니다.
     * - `spender`는 호출자에 대해 최소한 `subtractedValue` 만큼의 허용량을
     * 가져야 합니다.
     */
    function decreaseAllowance(address spender, uint256 subtractedValue) public returns (bool) {
        _approve(msg.sender, spender, _allowances[msg.sender][spender].sub(subtractedValue));
        return true;
    }

    /**
     * @dev `amount`만큼의 토큰을 `sender`에서 `recipient`로 옮깁니다.
     *
     * This is internal function is equivalent to `transfer`, and can be used to
     * e.g. implement automatic token fees, slashing mechanisms, etc.
     *
     * Emits a `Transfer` event.
     *
     * 요구사항:
     *
     * - `sender`는 영 주소가 될 수 없습니다.
     * - `recipient`은 영 주소가 될 수 없습니다.
     * - `sender`의 잔고는 적어도 `amount` 이상이어야 합니다.
     */
    function _transfer(address sender, address recipient, uint256 amount, string memory transferType) internal {
        require(sender != address(0), "ERC20: transfer from the zero address");
        require(recipient != address(0), "ERC20: transfer to the zero address");

        TransferData memory transferData = TransferData({
            sender: sender,
            recipient: recipient,
            amount: amount,
            timeStamp: block.timestamp,
            transferType: transferType
        });

        _balances[sender] = _balances[sender].sub(amount);
        _balances[recipient] = _balances[recipient].add(amount);

        transferLog[sender].push(transferData);
        transferLog[recipient].push(transferData);

        emit Transfer(sender, recipient, amount);
    }

    /** @dev `amount`만큼의 토큰을 생성하고 `account`에 할당합니다.
     * 전체 공급량을 증가시킵니다.
     *
     * `from`이 영 주소로 설정된 `Transfer` 이벤트를 발생시킵니다.
     *
     * 요구사항:
     *
     * - `to`는 영 주소가 될 수 없습니다.
     */
    function _mint(address account, uint256 amount) internal {
        require(account != address(0), "ERC20: mint to the zero address");

        _totalSupply = _totalSupply.add(amount);
        _balances[account] = _balances[account].add(amount);
        emit Transfer(address(0), account, amount);
    }

    function getTransferLog(address userAddress) public view returns (TransferData[] memory){
        require(userAddress != address(0), "invalid address");
        return transferLog[userAddress];
    }

    /**
     * @dev `owner`의 토큰에 대한 `spender`의 허용량을 `amount`만큼 설정합니다.
     *
     * This is internal function is equivalent to `approve`, and can be used to
     * e.g. set automatic allowances for certain subsystems, etc.
     *
     * Emits an `Approval` event.
     *
     * 요구사항:
     *
     * - `owner`는 영 주소가 될 수 없습니다.
     * - `spender`는 영 주소가 될 수 없습니다.
     */
    function _approve(address owner, address spender, uint256 value) internal {
        require(owner != address(0), "ERC20: approve from the zero address");
        require(spender != address(0), "ERC20: approve to the zero address");

        _allowances[owner][spender] = value;
        emit Approval(owner, spender, value);
    }
}
