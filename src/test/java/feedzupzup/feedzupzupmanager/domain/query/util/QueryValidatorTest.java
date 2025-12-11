package feedzupzup.feedzupzupmanager.domain.query.util;

import feedzupzup.feedzupzupmanager.domain.query.exception.NotExecuteQueryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("QueryValidator 테스트")
class QueryValidatorTest {

    @Nested
    @DisplayName("안전한 쿼리 검증")
    class SafeQueryTest {

        @Test
        @DisplayName("SELECT 쿼리는 통과한다")
        void selectQuery() {
            // given
            String sql = "SELECT * FROM users WHERE id = 1";

            // when & then
            assertThatCode(() -> QueryValidator.validate(sql))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("INSERT 쿼리는 통과한다")
        void insertQuery() {
            // given
            String sql = "INSERT INTO users (name, email) VALUES ('test', 'test@example.com')";

            // when & then
            assertThatCode(() -> QueryValidator.validate(sql))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("UPDATE 쿼리는 통과한다")
        void updateQuery() {
            // given
            String sql = "UPDATE users SET name = 'updated' WHERE id = 1";

            // when & then
            assertThatCode(() -> QueryValidator.validate(sql))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("복잡한 JOIN 쿼리는 통과한다")
        void complexJoinQuery() {
            // given
            String sql = "SELECT u.name, o.order_id FROM users u JOIN orders o ON u.id = o.user_id";

            // when & then
            assertThatCode(() -> QueryValidator.validate(sql))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("서브쿼리를 포함한 SELECT는 통과한다")
        void subquery() {
            // given
            String sql = "SELECT * FROM users WHERE id IN (SELECT user_id FROM orders)";

            // when & then
            assertThatCode(() -> QueryValidator.validate(sql))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("위험한 쿼리 검증")
    class DangerousQueryTest {

        @ParameterizedTest
        @ValueSource(strings = {
                "DROP TABLE users",
                "drop table users",
                "DROP DATABASE test_db",
                "DROP INDEX idx_name"
        })
        @DisplayName("DROP 쿼리는 예외를 발생시킨다")
        void dropQuery(String sql) {
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "TRUNCATE TABLE users",
                "truncate table users",
                "TRUNCATE users"
        })
        @DisplayName("TRUNCATE 쿼리는 예외를 발생시킨다")
        void truncateQuery(String sql) {
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "DELETE FROM users",
                "delete from users where id = 1",
                "DELETE FROM users WHERE id IN (1, 2, 3)"
        })
        @DisplayName("DELETE 쿼리는 예외를 발생시킨다")
        void deleteQuery(String sql) {
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "ALTER TABLE users ADD COLUMN age INT",
                "alter table users drop column email",
                "ALTER TABLE users MODIFY COLUMN name VARCHAR(100)"
        })
        @DisplayName("ALTER 쿼리는 예외를 발생시킨다")
        void alterQuery(String sql) {
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "GRANT ALL PRIVILEGES ON database.* TO 'user'@'localhost'",
                "grant select on users to test_user",
                "GRANT INSERT, UPDATE ON *.* TO 'admin'@'%'"
        })
        @DisplayName("GRANT 쿼리는 예외를 발생시킨다")
        void grantQuery(String sql) {
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "REVOKE ALL PRIVILEGES ON *.* FROM 'user'@'localhost'",
                "revoke select on users from test_user",
                "REVOKE INSERT ON database.* FROM 'admin'@'%'"
        })
        @DisplayName("REVOKE 쿼리는 예외를 발생시킨다")
        void revokeQuery(String sql) {
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "COMMIT",
                "commit",
                "COMMIT WORK"
        })
        @DisplayName("COMMIT 쿼리는 예외를 발생시킨다")
        void commitQuery(String sql) {
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "ROLLBACK",
                "rollback",
                "ROLLBACK WORK"
        })
        @DisplayName("ROLLBACK 쿼리는 예외를 발생시킨다")
        void rollbackQuery(String sql) {
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "RENAME TABLE users TO customers",
                "rename table old_name to new_name",
                "RENAME TABLE t1 TO t2, t3 TO t4"
        })
        @DisplayName("RENAME 쿼리는 예외를 발생시킨다")
        void renameQuery(String sql) {
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @Test
        @DisplayName("대소문자 혼합 위험 쿼리도 검증한다")
        void mixedCaseQuery() {
            // given
            String sql = "DrOp TaBlE users";

            // when & then
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }

        @Test
        @DisplayName("여러 줄의 위험 쿼리도 검증한다")
        void multiLineQuery() {
            // given
            String sql = """
                    SELECT * FROM users;
                    DROP TABLE users;
                    """;

            // when & then
            assertThatThrownBy(() -> QueryValidator.validate(sql))
                    .isInstanceOf(NotExecuteQueryException.class)
                    .hasMessageContaining("기존 테이블에 영향을 미칠 수 있어 실행할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("엣지 케이스 검증")
    class EdgeCaseTest {

        @Test
        @DisplayName("빈 문자열은 통과한다")
        void emptyString() {
            // given
            String sql = "";

            // when & then
            assertThatCode(() -> QueryValidator.validate(sql))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("컬럼명이나 테이블명에 위험 키워드가 포함된 경우는 통과한다")
        void dangerousKeywordInIdentifier() {
            // given
            String sql = "SELECT deleted_at, dropoff_location FROM users";

            // when & then
            assertThatCode(() -> QueryValidator.validate(sql))
                    .doesNotThrowAnyException();
        }
    }
}
