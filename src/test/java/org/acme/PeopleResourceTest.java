package org.acme;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.ws.rs.NotFoundException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@DisplayName("People Resource")
class PeopleResourceTest implements WithAssertions {

    @Mock
    PeopleRepository repository;

    @Mock
    RedisDataSource redis;

    @Mock
    ValueCommands<String, Person> valueCommands;

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

        @Nested
        @DisplayName("And cache contains person 1")
        class CacheContainsPerson1 {

            @Test
            @DisplayName("It should return what is the cache")
            void should_Return_What_Is_In_The_Cache() {
                // Given
                when(redis.value(Person.class)).thenReturn(valueCommands);
                when(valueCommands.get("person:1")).thenReturn(person1);

                // When
                var result = new PeopleResource(repository, redis).get(1L);

                // Then
                assertThat(result).isNotNull().isEqualTo(person1);
                verifyNoInteractions(repository);
                verify(redis, atMostOnce()).value(Person.class);
                verifyNoMoreInteractions(redis);
                verify(valueCommands, atMostOnce()).get("person:1");
                verifyNoMoreInteractions(valueCommands);
            }
        }

        @Nested
        @DisplayName("And cache does not contain person 1")
        class CacheDoesNotContainPerson1 {

            @Nested
            @DisplayName("And database contains person 1")
            class DatabaseContainsPerson1 {

                @Test
                @DisplayName("It should return person 1 from the database and store it in cache")
                void should_Return_Person1_From_Database_And_Store_It_In_Cache() {
                    // Given
                    when(repository.findById(1L)).thenReturn(person1);
                    when(redis.value(Person.class)).thenReturn(valueCommands);
                    when(valueCommands.get("person:1")).thenReturn(null);

                    // When
                    var result = new PeopleResource(repository, redis).get(1L);

                    // Then
                    assertThat(result).isNotNull().isEqualTo(person1);
                    verify(repository, atMostOnce()).findById(1L);
                    verifyNoMoreInteractions(repository);
                    verify(redis, atMostOnce()).value(Person.class);
                    verifyNoMoreInteractions(redis);
                    verify(valueCommands, atMostOnce()).get("person:1");
                    verify(valueCommands, atMostOnce()).setex("person:1", 60, person1);
                    verifyNoMoreInteractions(valueCommands);
                }
            }

            @Nested
            @DisplayName("And database does not contain person 1")
            class DatabaseDoesNotContainsPerson1 {

                @Test
                @DisplayName("It should return 404 Not Found")
                void should_Return_404_Not_Found() {
                    // Given
                    when(repository.findById(1L)).thenReturn(null);
                    when(redis.value(Person.class)).thenReturn(valueCommands);
                    when(valueCommands.get("person:1")).thenReturn(null);

                    // When
                    var exception = assertThatThrownBy(() -> new PeopleResource(repository, redis).get(1L));

                    // Then
                    exception.isInstanceOf(NotFoundException.class).hasMessage("Not Found");
                    verify(repository, atMostOnce()).findById(1L);
                    verifyNoMoreInteractions(repository);
                    verify(redis, atMostOnce()).value(Person.class);
                    verifyNoMoreInteractions(redis);
                    verify(valueCommands, atMostOnce()).get("person:1");
                    verifyNoMoreInteractions(valueCommands);
                }
            }
        }
    }
}
