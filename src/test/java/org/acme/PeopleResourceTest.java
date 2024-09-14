package org.acme;

import jakarta.ws.rs.NotFoundException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DisplayName("People Resource")
class PeopleResourceTest implements WithAssertions {

    @Mock
    PeopleService service;

    static final Person person1 = new Person(
            1L,
            "Person 1",
            LocalDate.of(2000, 1, 1),
            Person.Status.Alive
    );

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this).close();
    }

    @Nested
    @DisplayName("When GET /people/1 is called")
    class GetPeopleById1 {

        @Test
        @DisplayName("It should return what the service returns")
        void should_Return_What_Service_Returns() {
            // Given
            when(service.get(1L)).thenReturn(Optional.of(person1));

            // When
            var result = new PeopleResource(service).get(1L);

            // Then
            assertThat(result).isNotNull().isEqualTo(person1);
            verify(service, atMostOnce()).get(1L);
            verifyNoMoreInteractions(service);
        }


        @Test
        @DisplayName("It should return 404 when service returns empty")
        void should_Return_404_When_Service_Returns_Empty() {
            // Given
            when(service.get(1L)).thenReturn(Optional.empty());

            // When
            var exception = assertThatThrownBy(() -> new PeopleResource(service).get(1L));

            // Then
            exception.isInstanceOf(NotFoundException.class)
                    .hasMessage("Not Found");
            verify(service, atMostOnce()).get(1L);
            verifyNoMoreInteractions(service);
        }
    }
}
