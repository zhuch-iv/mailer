package net.zhu4.mailer.domain

import net.zhu4.mailer.domain.Template.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document


@Document(COLLECTION_NAME)
class Template(
    val id: ObjectId,
    val name: String,
    val template: String,
) {

    companion object {
        const val COLLECTION_NAME = "template"
    }
}
