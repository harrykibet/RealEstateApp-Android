package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.datastore.EstatiaPreferencesDataSource
import com.estatia.realestate.apps.core.domain.common.IExceptionTranslator
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import com.estatia.realestate.apps.core.testing.fixtures.UserFixtures
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var remoteDataSource: IUserRemoteDataSource
    private lateinit var estatiaPreferencesDataSource: EstatiaPreferencesDataSource
    private lateinit var metricsTracker: IMetricsTracker
    private lateinit var exceptionTranslator: IExceptionTranslator
    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        remoteDataSource = mockk()
        estatiaPreferencesDataSource = mockk()
        metricsTracker = mockk(relaxed = true)
        exceptionTranslator = mockk(relaxed = true)

        every { estatiaPreferencesDataSource.userData } returns kotlinx.coroutines.flow.flowOf(
            com.estatia.realestate.apps.core.model.user.UserData(
                likedProperties = emptySet(),
                bookmarkedProperties = emptySet(),
                viewedProperties = emptySet(),
                followedProperties = emptySet(),
                themeBrand = com.estatia.realestate.apps.core.model.utils.ThemeBrand.DEFAULT,
                darkThemeConfig = com.estatia.realestate.apps.core.model.utils.DarkThemeConfig.FOLLOW_SYSTEM,
                useDynamicColor = false,
                shouldHideOnboarding = true,
                isMuted = false
            )
        )
        
        repository = UserRepository(
            remoteDataSource,
            estatiaPreferencesDataSource,
            metricsTracker,
            exceptionTranslator
        )
    }

    @Test
    fun `getUserById returns mapped domain user from fixtures`() = runTest {
        val fixtureUser = UserFixtures.list().first()
        val userId = fixtureUser.userId!!
        
        coEvery { remoteDataSource.getUserById(userId) } returns AppResult.Success(
            UserEntityModel(
                userId = userId,
                name = fixtureUser.name,
                email = fixtureUser.email
                // Map other fields as needed
            )
        )

        val result = repository.getUserById(userId).assertSuccess()
        
        assertEquals(fixtureUser.name, result.name)
    }
}
