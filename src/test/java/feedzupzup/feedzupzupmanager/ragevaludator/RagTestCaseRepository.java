package feedzupzup.feedzupzupmanager.ragevaludator;

import java.util.List;

/**
 * RAG 평가용 테스트 케이스 저장소
 */
public class RagTestCaseRepository {

    public static List<RagTestCase> getTestCases() {
        return List.of(
                // ===== 관리자 인증 관련 (5개) =====
                RagTestCase.of(
                        "관리자 로그인 API는 어떻게 호출하나요?",
                        "POST /admin/login 엔드포인트를 사용하여 loginId와 password를 전송하면 JSESSIONID 쿠키가 설정됩니다."
                ),
                RagTestCase.of(
                        "관리자 회원가입은 어떻게 하나요?",
                        "POST /admin/sign-up 엔드포인트에 loginId, password, adminName을 전송하면 자동으로 로그인됩니다."
                ),
                RagTestCase.of(
                        "관리자 로그아웃 API는?",
                        "POST /admin/logout을 호출하면 세션이 삭제됩니다."
                ),
                RagTestCase.of(
                        "현재 로그인한 관리자 정보를 조회하려면?",
                        "GET /admin/me를 호출하면 현재 로그인한 관리자의 정보를 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "관리자 회원탈퇴는 어떻게 하나요?",
                        "DELETE /admin을 호출하면 관리자 회원탈퇴가 진행됩니다."
                ),

                // ===== 단체 관리 (8개) =====
                RagTestCase.of(
                        "단체를 생성하는 API는?",
                        "POST /admin/organizations를 사용하여 organizationName과 categories를 전송하면 단체가 생성됩니다."
                ),
                RagTestCase.of(
                        "내가 속한 단체 목록을 조회하려면?",
                        "GET /admin/organizations를 호출하면 본인이 속한 모든 단체를 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "단체 정보를 수정하는 API는?",
                        "PUT /admin/organizations/{organizationUuid}를 사용하여 단체명과 카테고리를 수정할 수 있습니다."
                ),
                RagTestCase.of(
                        "단체를 삭제하는 방법은?",
                        "DELETE /admin/organizations/{organizationUuid}를 호출하면 단체가 삭제됩니다."
                ),
                RagTestCase.of(
                        "특정 단체 정보를 조회하는 API는?",
                        "GET /organizations/{organizationUuid}를 호출하면 단체 이름, 응원 횟수, 카테고리 정보를 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "단체 통계를 확인하려면 어떤 API를 사용하나요?",
                        "GET /organizations/{organizationUuid}/statistic을 호출하면 반영률, 완료/대기 피드백 수를 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "단체에 응원하기 기능이 있나요?",
                        "POST /organizations/{organizationUuid}/cheer를 사용하여 cheeringCount를 전송하면 응원할 수 있습니다."
                ),
                RagTestCase.of(
                        "단체의 QR 코드를 조회하는 API는?",
                        "GET /admin/organizations/{organizationUuid}/qr-code를 호출하면 QR 이미지 URL과 사이트 URL을 조회할 수 있습니다."
                ),

                // ===== 피드백 생성/조회 (10개) =====
                RagTestCase.of(
                        "피드백을 생성하는 API는?",
                        "POST /organizations/{organizationUuid}/feedbacks를 사용하여 content, userName, category 등을 전송하면 피드백이 생성됩니다."
                ),
                RagTestCase.of(
                        "피드백 목록을 조회하려면?",
                        "GET /organizations/{organizationUuid}/feedbacks를 호출하면 특정 단체의 피드백 목록을 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "관리자용 피드백 목록 조회 API는?",
                        "GET /admin/organizations/{organizationUuid}/feedbacks를 호출하면 관리자 권한으로 피드백을 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "내가 쓴 피드백만 보려면?",
                        "GET /organizations/{organizationUuid}/feedbacks/my를 호출하면 본인이 작성한 피드백만 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "피드백을 삭제하는 API는?",
                        "DELETE /admin/feedbacks/{feedbackId}를 호출하면 관리자 권한으로 피드백을 삭제할 수 있습니다."
                ),
                RagTestCase.of(
                        "피드백에 답글을 추가하려면?",
                        "PATCH /admin/feedbacks/{feedbackId}/comment를 사용하여 comment를 전송하면 답글이 추가됩니다."
                ),
                RagTestCase.of(
                        "피드백 정렬 옵션에는 어떤 것들이 있나요?",
                        "sortBy 파라미터로 LATEST, OLDEST, LIKES를 사용할 수 있습니다."
                ),
                RagTestCase.of(
                        "비밀 피드백을 생성할 수 있나요?",
                        "피드백 생성 시 isSecret을 true로 설정하면 비밀 피드백으로 생성됩니다."
                ),
                RagTestCase.of(
                        "피드백에 이미지를 첨부할 수 있나요?",
                        "피드백 생성 시 imageUrl 필드에 이미지 URL을 포함하면 이미지가 첨부됩니다."
                ),
                RagTestCase.of(
                        "피드백 상태에는 어떤 것들이 있나요?",
                        "WAITING(대기중)과 CONFIRMED(완료됨) 두 가지 상태가 있습니다."
                ),

                // ===== 좋아요 기능 (5개) =====
                RagTestCase.of(
                        "피드백에 좋아요를 누르는 API는?",
                        "PATCH /feedbacks/{feedbackId}/like를 호출하면 해당 피드백에 좋아요가 추가됩니다."
                ),
                RagTestCase.of(
                        "피드백 좋아요를 취소하려면?",
                        "PATCH /feedbacks/{feedbackId}/unlike를 호출하면 좋아요가 취소됩니다."
                ),
                RagTestCase.of(
                        "내가 좋아요 누른 피드백 목록을 보려면?",
                        "GET /organizations/{organizationUuid}/feedbacks/my-likes를 호출하면 좋아요 기록을 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "좋아요 순으로 피드백을 정렬할 수 있나요?",
                        "피드백 조회 시 sortBy=LIKES를 사용하면 좋아요 순으로 정렬됩니다."
                ),
                RagTestCase.of(
                        "좋아요 기능은 인증이 필요한가요?",
                        "좋아요 기능은 게스트 사용자도 사용 가능하며, 쿠키로 추적됩니다."
                ),

                // ===== 클러스터링 기능 (6개) =====
                RagTestCase.of(
                        "클러스터 대표 피드백을 조회하는 API는?",
                        "GET /admin/organizations/{organizationUuid}/clusters를 호출하면 각 클러스터의 대표 피드백을 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "특정 클러스터의 모든 피드백을 보려면?",
                        "GET /admin/organizations/{organizationUuid}/clusters/{clusterId}를 호출하면 해당 클러스터의 전체 피드백을 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "클러스터링 배치 작업을 실행하는 API는?",
                        "PATCH /develop/feedbacks/clustering/batch를 호출하면 기존 피드백에 대한 클러스터링 작업이 시작됩니다."
                ),
                RagTestCase.of(
                        "클러스터 개수를 제한할 수 있나요?",
                        "클러스터 조회 시 limit 파라미터로 반환할 클러스터 개수를 제한할 수 있으며, 기본값은 5입니다."
                ),
                RagTestCase.of(
                        "클러스터 라벨은 어떻게 생성되나요?",
                        "각 클러스터의 대표 글이 자동으로 라벨로 설정됩니다."
                ),
                RagTestCase.of(
                        "클러스터에 속한 피드백 개수를 알 수 있나요?",
                        "클러스터 조회 시 totalCount 필드에 해당 클러스터에 속한 피드백 개수가 포함됩니다."
                ),

                // ===== 파일 다운로드/업로드 (5개) =====
                RagTestCase.of(
                        "피드백을 파일로 다운로드할 수 있나요?",
                        "POST /admin/organizations/{organizationUuid}/download-jobs로 작업을 시작하고, GET으로 진행 상태를 확인할 수 있습니다."
                ),
                RagTestCase.of(
                        "파일 생성 진행도를 확인하는 API는?",
                        "GET /admin/organizations/{organizationUuid}/download-jobs/{jobId}/status를 호출하면 진행률을 확인할 수 있습니다."
                ),
                RagTestCase.of(
                        "생성된 파일을 다운로드하려면?",
                        "GET /admin/organizations/{organizationUuid}/download-jobs/{jobId}를 호출하면 S3 Presigned URL로 리다이렉트됩니다."
                ),
                RagTestCase.of(
                        "이미지 업로드를 위한 URL을 받으려면?",
                        "POST /presigned-url에 objectDir과 extension을 전송하면 S3 presigned URL을 발급받을 수 있습니다."
                ),
                RagTestCase.of(
                        "QR 코드를 다운로드하려면?",
                        "GET /admin/organizations/{organizationUuid}/qr-code/download-url을 호출하면 다운로드 URL을 조회할 수 있습니다."
                ),

                // ===== 알림 기능 (4개) =====
                RagTestCase.of(
                        "알림 토큰을 등록하는 API는?",
                        "POST /admin/notifications/token에 notificationToken을 전송하면 FCM 토큰이 등록됩니다."
                ),
                RagTestCase.of(
                        "알림 설정을 조회하려면?",
                        "GET /admin/notifications/settings를 호출하면 현재 알림 on/off 상태를 조회할 수 있습니다."
                ),
                RagTestCase.of(
                        "알림을 끄려면 어떻게 하나요?",
                        "PATCH /admin/notifications/settings에 alertsOn을 false로 전송하면 알림이 꺼집니다."
                ),
                RagTestCase.of(
                        "알림 기능은 관리자 전용인가요?",
                        "네, 알림 기능은 관리자 인증이 필요한 API입니다."
                ),

                // ===== SSE 연결 (3개) =====
                RagTestCase.of(
                        "실시간 피드백 알림을 받으려면?",
                        "GET /sse/subscribe/{organizationUuid}로 SSE 연결을 수립하면 실시간 알림을 받을 수 있습니다."
                ),
                RagTestCase.of(
                        "관리자용 SSE 연결 API는?",
                        "GET /admin/sse/subscribe/{organizationUuid}를 사용하면 관리자 페이지에서 SSE 연결을 수립할 수 있습니다."
                ),
                RagTestCase.of(
                        "SSE 연결이 끊어지면 어떻게 되나요?",
                        "존재하지 않는 조직으로 연결 시도 시 404 오류가 반환되며 연결이 실패합니다."
                ),

                // ===== 페이지네이션/필터링 (4개) =====
                RagTestCase.of(
                        "피드백 목록에서 커서 페이지네이션을 사용하려면?",
                        "cursorId와 size 파라미터를 사용하여 다음 페이지를 조회할 수 있으며, hasNext로 다음 페이지 존재 여부를 확인합니다."
                ),
                RagTestCase.of(
                        "대기 중인 피드백만 필터링하려면?",
                        "status=WAITING 파라미터를 추가하면 대기 중인 피드백만 조회됩니다."
                ),
                RagTestCase.of(
                        "완료된 피드백만 보려면?",
                        "status=CONFIRMED 파라미터를 사용하면 완료된 피드백만 필터링됩니다."
                ),
                RagTestCase.of(
                        "한 페이지에 몇 개의 피드백이 조회되나요?",
                        "size 파라미터로 조정 가능하며, 기본값은 10개입니다."
                ),

                // ===== 개발자 API (2개) =====
                RagTestCase.of(
                        "관리자 비밀번호를 변경하는 개발자 API는?",
                        "PATCH /develop/change-password에 developerPassword, loginId, changePasswordValue를 전송하면 비밀번호가 변경됩니다."
                ),
                RagTestCase.of(
                        "개발자 API는 어떻게 인증하나요?",
                        "secret 파라미터로 개발자 비밀번호를 전송하여 인증합니다."
                ),

                // ===== 에러 처리 (3개) =====
                RagTestCase.of(
                        "인증이 필요한 API를 호출했을 때 로그인하지 않으면?",
                        "401 Unauthorized 에러가 반환되며, 관리자 로그인 후 세션을 통해 인증해야 합니다."
                ),
                RagTestCase.of(
                        "존재하지 않는 피드백을 조회하면?",
                        "404 Not Found 에러가 반환됩니다."
                ),
                RagTestCase.of(
                        "이미 좋아요를 누른 피드백에 다시 좋아요를 누르면?",
                        "409 Conflict 에러가 반환됩니다."
                ),

                // ===== 🔴 존재하지 않는 정보 (Negative Cases) - 15개 =====
                RagTestCase.of(
                        "피드백을 수정하는 API는?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "사용자 프로필을 조회하는 API는?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "댓글을 삭제하는 API는?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "피드백에 태그를 추가할 수 있나요?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "단체 멤버를 초대하는 API는?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "관리자 권한을 변경하는 방법은?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "피드백 검색 기능이 있나요?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "결제 정보를 등록하는 API는?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "구독을 취소하는 방법은?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "사용자 차단 기능이 있나요?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "피드백을 공유하는 API는?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "대시보드 통계를 CSV로 내보낼 수 있나요?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "이메일 알림 설정을 변경하려면?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "피드백에 별점을 매길 수 있나요?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                ),
                RagTestCase.of(
                        "GraphQL API를 지원하나요?",
                        "해당 정보는 API 문서에서 찾을 수 없습니다."
                )
        );
    }

    /**
     * 단일 테스트 케이스
     */
    public static class RagTestCase {

        private final String query;
        private final String expectedAnswer;

        private RagTestCase(String query, String expectedAnswer) {
            this.query = query;
            this.expectedAnswer = expectedAnswer;
        }

        public static RagTestCase of(String query, String expectedAnswer) {
            return new RagTestCase(query, expectedAnswer);
        }

        public String getQuery() {
            return query;
        }

        public String getExpectedAnswer() {
            return expectedAnswer;
        }

        @Override
        public String toString() {
            return "RagTestCase{" +
                    "query='" + query + '\'' +
                    ", expectedAnswer='" + expectedAnswer + '\'' +
                    '}';
        }
    }
}