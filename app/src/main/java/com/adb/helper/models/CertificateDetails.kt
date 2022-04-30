package com.adb.helper.models

import com.adb.helper.utils.DigestHelper
import java.security.cert.X509Certificate
import java.util.*
import java.util.regex.Pattern
import javax.security.auth.x500.X500Principal

data class CertificateDetails constructor(@Transient val certificate: X509Certificate) {
    private val signAlgorithm: String = certificate.sigAlgName
    private val certificateHash: String
    private val publicKeyMd5: String
    private val startDate: Date
    private val endDate: Date
    private var serialNumber: Int = 0
    private var issuerName: String? = null
    private var issuerOrganization: String? = null
    private var issuerCountry: String? = null
    private var subjectName: String? = null
    private var subjectOrganization: String? = null
    private var subjectCountry: String? = null

    init {
        val digestManager = DigestHelper()
        certificateHash = digestManager.md5Digest(certificate.encoded)
        publicKeyMd5 =
            digestManager.md5Digest(digestManager.byteToHexString(certificate.publicKey.encoded))
        startDate = certificate.notBefore
        endDate = certificate.notAfter
        serialNumber = certificate.serialNumber.toInt()
        issuerName = certificate.issuerX500Principal?.getPrincipalCommonName()
        issuerOrganization = certificate.issuerX500Principal?.getPrincipalOrganization()
        issuerCountry = certificate.issuerX500Principal?.getPrincipalCountry()
        subjectName = certificate.subjectX500Principal?.getPrincipalCommonName()
        subjectOrganization = certificate.subjectX500Principal?.getPrincipalOrganization()
        subjectCountry = certificate.subjectX500Principal?.getPrincipalCountry()
    }

    private fun X500Principal.getPrincipalCommonName(): String? {
        val name = getName(X500Principal.RFC1779)
        return if (name.isNullOrBlank()) null else parsePrincipal(name, "CN=([^,]*)")
    }

    private fun X500Principal.getPrincipalOrganization(): String? {
        val name = getName(X500Principal.RFC1779)
        return if (name.isNullOrBlank()) null else parsePrincipal(name, "O=([^,]*)")
    }

    private fun X500Principal.getPrincipalCountry(): String? {
        val name: String? = getName(X500Principal.RFC1779)
        return if (name.isNullOrBlank()) null else parsePrincipal(name, "C=([^,]*)")
    }

    private fun parsePrincipal(principalName: String, patternString: String): String? {
        val matcher = Pattern.compile(patternString).matcher(principalName)
        return if (matcher.find()) {
            matcher.group(1)
        } else null
    }
}