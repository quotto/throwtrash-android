package net.mythrowaway.app

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Sample::class)
class SampleTest {
    val instance = PowerMockito.mock(Sample::class.java)
    val messageCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
    val valueCaptor: ArgumentCaptor<Int> = ArgumentCaptor.forClass(Int::class.java)

    @Test
    fun test() {
        instance.testTarget("This is", 3)

        Mockito.verify(instance,Mockito.times(1)).testTarget(messageCaptor.capture(),valueCaptor
            .capture())

        Assert.assertEquals("This is", messageCaptor.value)
        Assert.assertEquals(3, valueCaptor.value)
    }
}