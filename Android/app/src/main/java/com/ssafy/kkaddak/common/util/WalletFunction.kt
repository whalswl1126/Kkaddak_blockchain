package com.ssafy.kkaddak.common.util

import android.util.Log
import org.bouncycastle.crypto.digests.KeccakDigest
import org.bouncycastle.util.encoders.Hex
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

private const val INFURA_URL = "https://rpc.ssafy-blockchain.com"
private const val CONTRACT_ADDRESS = "0xD2ec852c1B011f4Fb286396830a9457d6DE3eBF4"
private var MY_WALLET_ADDRESS = ""
private const val TAG = "wallet info"

fun generateWallet() {
    Thread {
        //Security.addProvider(BouncyCastleProvider())
        // ECDSA 키 쌍 생성
        val ecKeyPair: ECKeyPair = Keys.createEcKeyPair()
        // 개인 키 추출
        val privateKey: String = ecKeyPair.privateKey.toString(16)
        // 공개 키 추출
        val publicKey: String = ecKeyPair.publicKey.toString(16)
        // Credentials 객체 생성
        val credentials: Credentials = Credentials.create(privateKey)

        // Ethereum 네트워크에 연결
        val web3j1 = Web3j.build(HttpService(INFURA_URL))
        //val transactionManager = RawTransactionManager(web3j1, ecKeyPair.publicKey)

        val address = generateAccountAddress(ecKeyPair.publicKey.toByteArray())

        Log.d(TAG, "Private key: $privateKey")
        Log.d(TAG, "Public key: $address")

        MY_WALLET_ADDRESS = address

        // Ethereum 네트워크에 연결
        val web3j = Web3j.build(HttpService(INFURA_URL))

        // 컨트랙트의 RemoteCall 객체 생성
        val contract = ERC20.load(
            CONTRACT_ADDRESS,
            web3j,
            Credentials.create(privateKey),
            DefaultGasProvider()
        )
        val call: RemoteCall<BigInteger>? = contract.balanceOf(address)

        try {
            // 컨트랙트에서 나의 지갑 잔액 및 토큰 이름 가져오기
            val result = call?.send()
            val balance = result.toString()
//            val tokenName = result.component2()

            // 잔액 계산
            val balanceDecimal = BigDecimal(balance)
            val balanceInEther = Convert.fromWei(balanceDecimal, Convert.Unit.ETHER)

            Log.d(TAG, "Balance of $address : $balanceInEther $balance")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "abc: ${e.toString()}")
        }
    }.start()
}

fun insertUserWallet(){
    Thread {
        // 개인 키 추출
        val privateKey: String = "b89cd06cf5acd5e0d1b1dc0c7e29233c318d42f8c77a3af82a8f3ff53ae1577c"
        val publicKey: String = "0xf10ccb49335c686147bdba507482bb3d3e3af1c4"

        // Ethereum 네트워크에 연결
        val web3j = Web3j.build(HttpService(INFURA_URL))

        //val transactionManager = RawTransactionManager(web3j, publicKey)

        /*val contract1 = ERC20.load(
            CONTRACT_ADDRESS,
            web3j,
            Address(CONTRACT_OWNER_ADDRESS,  web3j.ethChainId().send()),
            DefaultGasProvider()
        )*/

//        여기서 CONTRACT_OWNER_ADDRESS는 컨트랙트 소유자의 주소를 나타내는 문자열 상수입니다.
//        EthereumContext.getChainId()는 이더리움 블록체인 네트워크의 체인 ID를 가져오는 데 사용됩니다.
//        주의할 점은, 해당 주소로 컨트랙트에 대한 모든 작업이 수행됩니다.
//        따라서 해당 주소에는 컨트랙트 작업에 필요한 충분한 이더가 있어야 합니다.
//        또한, 해당 주소는 컨트랙트의 소유자가 아닐 수도 있습니다.

        // 컨트랙트의 RemoteCall 객체 생성
        val contract = ERC20.load(
            CONTRACT_ADDRESS,
            web3j,
            Credentials.create(privateKey),
            DefaultGasProvider()
        )
        val call: RemoteCall<BigInteger>? = contract.balanceOf(publicKey)

        try {
            // 컨트랙트에서 나의 지갑 잔액 및 토큰 이름 가져오기
            val result = call?.send()
            val balance = result.toString()
//            val tokenName = result.component2()

            // 잔액 계산
            val balanceDecimal = BigDecimal(balance)
            val balanceInEther = Convert.fromWei(balanceDecimal, Convert.Unit.ETHER)

            Log.d(TAG, "Balance of $publicKey : $balanceInEther $balance")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "abc: ${e.toString()}")
        }
    }.start()
}

fun generateAccountAddress(publicKey: ByteArray): String {
    // Add 04 prefix to indicate that it's uncompressed public key.
    val uncompressedPublicKey = byteArrayOf(0x04) + publicKey
    // Keccak-256 hash function is used to hash the public key.
    val keccak256Digest = KeccakDigest(256)
    val hashedPublicKey = ByteArray(keccak256Digest.digestSize)
    keccak256Digest.update(uncompressedPublicKey, 0, uncompressedPublicKey.size)
    keccak256Digest.doFinal(hashedPublicKey, 0)
    // Only the last 20 bytes are used as the account address.
    val address = hashedPublicKey.copyOfRange(hashedPublicKey.size - 20, hashedPublicKey.size)
    // Convert byte array to hexadecimal string.
    val addressHex = Hex.toHexString(address)
    // Add "0x" prefix to indicate that it's a hexadecimal string.
    return "0x$addressHex"
}