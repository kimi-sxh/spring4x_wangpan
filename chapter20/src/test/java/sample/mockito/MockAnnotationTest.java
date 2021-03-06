package sample.mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.smart.domain.User;

public class MockAnnotationTest {

    @Mock
    User mockUser;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testMockUser() {
        when(mockUser.getUserId()).thenReturn(1);
        when(mockUser.getUserName()).thenReturn("tom");
        assertEquals(mockUser.getUserId(), 1);
        assertEquals(mockUser.getUserName(), "tom");
    }
}
