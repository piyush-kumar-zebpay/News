package com.example.news.data.local

import androidx.datastore.core.Serializer
import com.example.news.data.model.Bookmarks
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ArticleListSerializer : Serializer<Bookmarks.ArticleList> {

    override val defaultValue: Bookmarks.ArticleList = Bookmarks.ArticleList.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Bookmarks.ArticleList {
        try {
            return Bookmarks.ArticleList.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw exception
        }
    }

    override suspend fun writeTo(t: Bookmarks.ArticleList, output: OutputStream) {
        t.writeTo(output)
    }
}
