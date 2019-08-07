package com.lightningkite.kabinet

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ObjectMetadata
import com.lightningkite.kommon.string.MediaType
import com.lightningkite.lokalize.time.TimeStamp
import kotlinx.io.core.Input
import kotlinx.io.core.Output
import kotlinx.io.streams.asInput
import kotlinx.io.streams.asOutput
import java.io.ByteArrayInputStream
import java.io.File

//TODO: Caching?
class S3FileSystem(val client: AmazonS3 = AmazonS3ClientBuilder.defaultClient(), val bucket: String): FileSystem {
    override suspend fun info(path: AbsolutePath): FileInformation? {
        return try {
            client.getObject(bucket, path.string).objectMetadata.let { d ->
                FileInformation(
                        fileSystem = this,
                        childrenPaths = null,
                        type = MediaType(d.contentType),
                        created = TimeStamp(d.lastModified.time),
                        lastModified = TimeStamp(d.lastModified.time),
                        readable = true,
                        writable = true,
                        length = d.contentLength
                )
            }
        } catch(e:Exception){
            e.printStackTrace()
            null
        }
    }

    override suspend fun overwriteAll(path: AbsolutePath, data: ByteArray) {
        val tempFile = File.createTempFile("s3upload", "s3upload")
        tempFile.writeBytes(data)
        client.putObject(bucket, path.string, tempFile)
        tempFile.delete()
    }

    override suspend fun overwrite(path: AbsolutePath): Output {
        val tempFile = File.createTempFile("s3upload", "s3upload")
        val basedOn = tempFile.outputStream().asOutput()
        return object : Output by basedOn {
            override fun close() {
                basedOn.close()
                client.putObject(bucket, path.string, tempFile)
                tempFile.delete()
            }
        }
    }

    override suspend fun readAll(path: AbsolutePath): ByteArray? {
        return try {
            client.getObject(bucket, path.string).objectContent.use { it.readBytes() }
        } catch(e:Exception){
            e.printStackTrace()
            null
        }
    }

    override suspend fun read(path: AbsolutePath): Input? {
        return try {
            client.getObject(bucket, path.string).objectContent.asInput()
        } catch(e:Exception){
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteIfExists(path: AbsolutePath): Boolean {
        return try {
            client.deleteObject(bucket, path.string)
            true
        } catch(e:Exception){
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteRecursivelyIfExists(path: AbsolutePath): Boolean {
        return try {
            var contToken: String? = null
            while(true){
                val results = client.listObjectsV2(ListObjectsV2Request()
                        .withBucketName(bucket)
                        .withPrefix(path.string)
                        .withContinuationToken(contToken)
                )
                client.deleteObjects(DeleteObjectsRequest(bucket).withKeys(results.objectSummaries.map { DeleteObjectsRequest.KeyVersion(it.key) }))
                if(results.isTruncated) {
                    contToken = results.nextContinuationToken
                } else {
                    break
                }
            }
            true
        }  catch(e:Exception){
            e.printStackTrace()
            false
        }
    }

    override suspend fun moveIfExists(path: AbsolutePath, newPath: AbsolutePath): Boolean {
        return try {
            var contToken: String? = null
            while(true){
                val results = client.listObjectsV2(ListObjectsV2Request()
                        .withBucketName(bucket)
                        .withPrefix(path.string)
                        .withContinuationToken(contToken)
                )
                if(contToken == null && results.keyCount == 0){
                    return false
                }
                for(obj in results.objectSummaries){
                    client.copyObject(bucket, path.string, bucket, newPath.string)
                }
                client.deleteObjects(DeleteObjectsRequest(bucket).withKeys(results.objectSummaries.map { DeleteObjectsRequest.KeyVersion(it.key) }))
                if(results.isTruncated) {
                    contToken = results.nextContinuationToken
                } else {
                    break
                }
            }
            true
        }  catch(e:Exception){
            e.printStackTrace()
            false
        }
    }
}