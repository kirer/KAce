package com.kace.media.infrastructure.persistence.repository

import com.kace.media.domain.model.MediaFolder
import com.kace.media.domain.repository.MediaFolderRepository
import com.kace.media.infrastructure.persistence.entity.MediaFolderEntity
import com.kace.media.infrastructure.persistence.entity.MediaFolders
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class MediaFolderRepositoryImpl : MediaFolderRepository {

    override fun findById(id: UUID): MediaFolder? = transaction {
        MediaFolderEntity.findById(id)?.toMediaFolder()
    }

    override fun findAll(page: Int, size: Int): List<MediaFolder> = transaction {
        MediaFolderEntity.all()
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { it.toMediaFolder() }
    }

    override fun findByParentId(parentId: UUID?, page: Int, size: Int): List<MediaFolder> = transaction {
        MediaFolderEntity.find {
            if (parentId != null) {
                MediaFolders.parentId eq parentId
            } else {
                MediaFolders.parentId.isNull()
            }
        }
        .limit(size, offset = ((page - 1) * size).toLong())
        .map { it.toMediaFolder() }
    }

    override fun findByName(name: String, parentId: UUID?, page: Int, size: Int): List<MediaFolder> = transaction {
        val query = if (parentId != null) {
            MediaFolders.name like "%$name%" and (MediaFolders.parentId eq parentId)
        } else {
            MediaFolders.name like "%$name%" and MediaFolders.parentId.isNull()
        }
        
        MediaFolderEntity.find(query)
            .limit(size, offset = ((page - 1) * size).toLong())
            .map { it.toMediaFolder() }
    }

    override fun save(folder: MediaFolder): MediaFolder = transaction {
        val existingFolder = folder.id?.let { MediaFolderEntity.findById(it) }

        val folderEntity = if (existingFolder != null) {
            existingFolder.apply {
                this.name = folder.name
                this.description = folder.description
                this.parentId = folder.parentId
                this.updatedAt = Instant.now()
            }
        } else {
            MediaFolderEntity.new(folder.id ?: UUID.randomUUID()) {
                this.name = folder.name
                this.description = folder.description
                this.parentId = folder.parentId
                this.createdAt = Instant.now()
                this.updatedAt = Instant.now()
                this.createdBy = folder.createdBy
            }
        }

        folderEntity.toMediaFolder()
    }

    override fun delete(id: UUID): Boolean = transaction {
        val folder = MediaFolderEntity.findById(id)
        folder?.delete()
        folder != null
    }

    override fun count(): Long = transaction {
        MediaFolderEntity.count()
    }

    override fun countByParentId(parentId: UUID?): Long = transaction {
        if (parentId != null) {
            MediaFolderEntity.find { MediaFolders.parentId eq parentId }.count()
        } else {
            MediaFolderEntity.find { MediaFolders.parentId.isNull() }.count()
        }
    }

    override fun hasChildren(id: UUID): Boolean = transaction {
        MediaFolderEntity.find { MediaFolders.parentId eq id }.count() > 0
    }

    private fun MediaFolderEntity.toMediaFolder(): MediaFolder {
        return MediaFolder(
            id = this.id.value,
            name = this.name,
            description = this.description,
            parentId = this.parentId,
            createdBy = this.createdBy,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
} 