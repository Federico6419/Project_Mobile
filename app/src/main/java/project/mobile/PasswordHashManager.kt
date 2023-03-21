package project.mobile

import java.security.NoSuchAlgorithmException
import java.math.BigInteger
import java.security.MessageDigest

class PasswordHashManager {

    fun encryptSHA256(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val messageDigest = md.digest(input.toByteArray())
            val no = BigInteger(1, messageDigest)
            var hashtext = no.toString(16)
            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }
            hashtext
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    fun main(args: Array<String>) {
        println("HashCode Generated by SHA-256 for: ")
        val s1 = "knowledgefactory.net"
        println("$s1 : ${encryptSHA256(s1)}"
        )
    }
}