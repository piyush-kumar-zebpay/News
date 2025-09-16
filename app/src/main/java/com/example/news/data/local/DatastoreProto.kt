package com.example.news.data.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.news.data.model.BookmarksOuterClass
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object BookmarksSerializer : Serializer<BookmarksOuterClass.Bookmarks> {
    override val defaultValue: BookmarksOuterClass.Bookmarks = BookmarksOuterClass.Bookmarks.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): BookmarksOuterClass.Bookmarks {
        try {
            return BookmarksOuterClass.Bookmarks.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }
    override suspend fun writeTo(t: BookmarksOuterClass.Bookmarks, output: OutputStream) {
        t.writeTo(output)
    }
}
