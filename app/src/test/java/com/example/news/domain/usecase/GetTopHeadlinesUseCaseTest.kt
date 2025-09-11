package com.example.news.domain.usecase

import com.example.news.domain.model.Article
import com.example.news.domain.model.Result
import com.example.news.domain.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTopHeadlinesUseCaseTest {

    private lateinit var newsRepository: NewsRepository
    private lateinit var getTopHeadlinesUseCase: GetTopHeadlinesUseCase

    @Before
    fun setup() {
        newsRepository = mockk()
        getTopHeadlinesUseCase = GetTopHeadlinesUseCase(newsRepository)
    }

    @Test
    fun `when repository returns success, use case should return success with articles`() = runBlocking {
        // Given
        val articles = listOf(
            Article(
                sourceName = "Test Source",
                sourceId = "test-id",
                author = "Test Author",
                title = "Test Title",
                description = "Test Description",
                url = "https://test.com",
                imageUrl = "https://test.com/image.jpg",
                publishedAt = "2025-09-08T10:00:00Z",
                content = "Test Content"
            )
        )
        coEvery { newsRepository.getTopHeadlines("us",) } returns Result.Success(articles)

        // When
        val result = getTopHeadlinesUseCase("us")

        // Then
        assertTrue(result is Result.Success)
        assertEquals(articles, (result as Result.Success).data)
    }

    @Test
    fun `when repository returns error, use case should return error`() = runBlocking {
        // Given
        val errorMessage = "Network error"
        coEvery { newsRepository.getTopHeadlines("us",) } returns Result.Error(errorMessage)

        // When
        val result = getTopHeadlinesUseCase("us")

        // Then
        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
    }

    @Test
    fun `when repository returns loading, use case should return loading`() = runBlocking {
        // Given
        coEvery { newsRepository.getTopHeadlines("us",) } returns Result.Loading

        // When
        val result = getTopHeadlinesUseCase("us")

        // Then
        assertTrue(result is Result.Loading)
    }

    @Test
    fun `when repository returns empty list, use case should return success with empty list`() = runBlocking {
        // Given
        val emptyArticles = emptyList<Article>()
        coEvery { newsRepository.getTopHeadlines("us",) } returns Result.Success(emptyArticles)

        // When
        val result = getTopHeadlinesUseCase("us")

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun `when different country code is provided, repository should be called with that country code`() = runBlocking {
        // Given
        val articles = emptyList<Article>()
        coEvery { newsRepository.getTopHeadlines("in",) } returns Result.Success(articles)

        // When
        val result = getTopHeadlinesUseCase("in")

        // Then
        assertTrue(result is Result.Success)
        // MockK automatically verifies that the repository was called with "in"
    }
}
