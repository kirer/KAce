package com.kace.media.infrastructure.persistence.repository

import com.kace.media.domain.model.Media
import com.kace.media.domain.repository.MediaRepository
import com.kace.media.infrastructure.persistence.entity.MediaEntity
import com.kace.media.infrastructure.persistence.entity.MediaTagEntity
import com.kace.media.infrastructure.persistence.entity.MediaTags
import com.kace.media.infrastructure.persistence.entity.Medias
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class MediaRepositoryImpl : MediaRepository {

    override fun findById(id: UUID): Media? = transaction {
        MediaEntity.findById(id)?.toMedia()
    }

    override fun findAll(page: Int, size: Int): List<Media> = transaction {
        MediaEntity.all()
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { it.toMedia() }
    }

    override fun findByFolderId(folderId: UUID?, page: Int, size: Int): List<Media> = transaction {
        MediaEntity.find {
            if (folderId != null) {
                Medias.folderId eq folderId
            } else {
                Medias.folderId.isNull()
            }
        }
        .limit(size, offset = ((page - 1) * size).toLong())
        .map { it.toMedia() }
    }

    override fun findByName(name: String, page: Int, size: Int): List<Media> = transaction {
        MediaEntity.find { Medias.name like "%$name%" }
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { it.toMedia() }
    }

    override fun findByTag(tag: String, page: Int, size: Int): List<Media> = transaction {
        (MediaEntity innerJoin MediaTagEntity innerJoin MediaTags)
            .select { MediaTags.name eq tag }
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { MediaEntity.wrapRow(it).toMedia() }
            .distinct()
    }

    override fun save(media: Media): Media = transaction {
        val existingMedia = media.id?.let { MediaEntity.findById(it) }

        val mediaEntity = if (existingMedia != null) {
            existingMedia.apply {
                this.name = media.name
                this.description = media.description
                this.type = media.type
                this.mimeType = media.mimeType
                this.size = media.size
                this.path = media.path
                this.url = media.url
                this.metadata = media.metadata
                this.folderId = media.folderId
                this.updatedAt = Instant.now()
            }
        } else {
            MediaEntity.new(media.id ?: UUID.randomUUID()) {
                this.name = media.name
                this.description = media.description
                this.type = media.type
                this.mimeType = media.mimeType
                this.size = media.size
                this.path = media.path
                this.url = media.url
                this.metadata = media.metadata
                this.folderId = media.folderId
                this.createdAt = Instant.now()
                this.updatedAt = Instant.now()
                this.createdBy = media.createdBy
            }
        }

        mediaEntity.toMedia()
    }

    override fun delete(id: UUID): Boolean = transaction {
        val media = MediaEntity.findById(id)
        media?.delete()
        media != null
    }

    override fun count(): Long = transaction {
        MediaEntity.count()
    }

    override fun countByFolderId(folderId: UUID?): Long = transaction {
        if (folderId != null) {
            MediaEntity.find { Medias.folderId eq folderId }.count()
        } else {
            MediaEntity.find { Medias.folderId.isNull() }.count()
        }
    }

    override fun addTag(mediaId: UUID, tagId: UUID): Boolean = transaction {
        try {
            MediaTagEntity.new {
                this.mediaId = mediaId
                this.tagId = tagId
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun removeTag(mediaId: UUID, tagId: UUID): Boolean = transaction {
        val count = MediaTagEntity.find {
            (MediaTags.mediaId eq mediaId) and (MediaTags.tagId eq tagId)
        }.count()
        
        if (count > 0) {
            MediaTagEntity.find {
                (MediaTags.mediaId eq mediaId) and (MediaTags.tagId eq tagId)
            }.first().delete()
            true
        } else {
            false
        }
    }

    private fun MediaEntity.toMedia(): Media {
        return Media(
            id = this.id.value,
            name = this.name,
            description = this.description,
            type = this.type,
            mimeType = this.mimeType,
            size = this.size,
            path = this.path,
            url = this.url,
            metadata = this.metadata,
            folderId = this.folderId,
            createdBy = this.createdBy,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
} 