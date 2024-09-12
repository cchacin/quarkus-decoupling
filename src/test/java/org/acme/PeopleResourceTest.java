package org.acme;

import jakarta.ws.rs.NotFoundException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

@DisplayName("People Resource")
class PeopleResourceTest implements WithAssertions {

    static final Person PERSON_1 = new Person(
            1L,
            "Person 1",
            LocalDate.of(2000, 1, 1),
            Person.Status.Alive
    );

    @Nested
    @DisplayName("When GET /people/1 is called")
    class GetPeopleById1 {

        @Test
        @DisplayName("It should return what the service returns")
        void should_Return_What_Service_Returns() {
            // Given
            var service = new HashMap<Long, Person>();
            service.put(1L, PERSON_1);

            // When
            var result = new PeopleResource(
                    (Long id) -> Optional.ofNullable(service.get(id)),
                    (Person person) -> service.put(person.getId(), person)
            ).get(1L);

            // Then
            assertThat(result).isNotNull().isEqualTo(PERSON_1);
        }


        @Test
        @DisplayName("It should return 404 when service returns empty")
        void should_Return_404_When_Service_Returns_Empty() {
            // Given
            var service = new HashMap<Long, Person>();

            // When
            var exception = assertThatThrownBy(
                    () -> new PeopleResource(
                            (Long id) -> Optional.ofNullable(service.get(id)),
                            (Person person) -> service.put(person.getId(), person)

                    ).get(1L)
            );

            // Then
            exception.isInstanceOf(NotFoundException.class)
                    .hasMessage("Not Found");
        }
    }
}
