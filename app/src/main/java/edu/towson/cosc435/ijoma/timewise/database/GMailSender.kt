/*
package edu.towson.cosc435.ijoma.timewise.sampledata

import java.util.Properties
import javax.mail.*
import javax.mail.internet.*

class GMailSender(private val username: String, private val password: String) {
    fun sendEmail(subject: String, message: String, recipients: Array<String>) {
        val properties = Properties()
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.starttls.enable"] = "true"
        properties["mail.smtp.host"] = "smtp.gmail.com"
        properties["mail.smtp.port"] = "587"

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        try {
            val mimeMessage = MimeMessage(session)
            mimeMessage.setFrom(InternetAddress(username))
            recipients.forEach { recipient ->
                mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(recipient))
            }
            mimeMessage.subject = subject
            mimeMessage.setText(message)

            Transport.send(mimeMessage)
            println("Email sent successfully!")
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }
}
*/