const DOMESTIC_MISSIONS = [
  {
    id: 1,
    title: '노숙자 급식 사역',
    description: '매월 셋째 주 토요일, 서울역 인근 노숙자들을 위한 무료 급식',
    location: '서울역 광장',
  },
  {
    id: 2,
    title: '지역아동센터 후원',
    description: '강남구 지역아동센터 3곳 정기 후원 및 멘토링',
    location: '강남구',
  },
  {
    id: 3,
    title: '장애인 복지관 봉사',
    description: '매주 수요일 장애인 복지관 청소 및 프로그램 지원',
    location: '성복장애인복지관',
  },
] as const;

const INTERNATIONAL_MISSIONS = [
  {
    id: 1,
    country: '필리핀',
    missionaries: 3,
    projects: '빈민가 교회 개척, 학교 건립',
  },
  {
    id: 2,
    country: '캄보디아',
    missionaries: 2,
    projects: '고아원 운영, 직업 훈련',
  },
  {
    id: 3,
    country: '네팔',
    missionaries: 2,
    projects: '교회 개척, 의료 사역',
  },
  {
    id: 4,
    country: '우간다',
    missionaries: 1,
    projects: '학교 건립, 식수 지원',
  },
] as const;

export function MissionSection() {
  return (
    <div className="space-y-16">
      {/* Domestic Missions */}
      <section>
        <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
          국내 선교
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {DOMESTIC_MISSIONS.map((mission) => (
            <div
              key={mission.id}
              className="bg-white rounded-xl border border-gray-200 p-8 hover:shadow-lg transition-shadow"
            >
              <h3 className="text-xl font-bold text-gray-900 mb-4">
                {mission.title}
              </h3>
              <p className="text-gray-700 mb-4 leading-relaxed">
                {mission.description}
              </p>
              <div className="flex items-center gap-2 text-sm text-primary-600 font-semibold">
                <span>📍</span>
                <span>{mission.location}</span>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* International Missions */}
      <section>
        <h2 className="text-3xl font-bold text-center text-gray-900 mb-12">
          해외 선교
        </h2>

        <div className="bg-white rounded-2xl border border-gray-200 p-8 md:p-12">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {INTERNATIONAL_MISSIONS.map((mission) => (
              <div
                key={mission.id}
                className="border-l-4 border-primary-500 pl-6"
              >
                <h3 className="text-2xl font-bold text-gray-900 mb-2">
                  {mission.country}
                </h3>
                <p className="text-sm text-primary-600 font-semibold mb-3">
                  파송 선교사: {mission.missionaries}명
                </p>
                <p className="text-gray-700">{mission.projects}</p>
              </div>
            ))}
          </div>

          <div className="mt-12 pt-8 border-t border-gray-200">
            <h4 className="text-lg font-bold text-gray-900 mb-4">
              선교 후원 안내
            </h4>
            <p className="text-gray-700 mb-4">
              성복교회는 15개국 40여 선교지를 지원하고 있습니다.
              <br />
              선교 사역에 동참하기 원하시는 분들은 교회 사무실로 문의해 주시기
              바랍니다.
            </p>
            <div className="bg-gray-50 rounded-lg p-6">
              <p className="text-sm text-gray-700">
                <strong>연락처:</strong> 02-1234-5678
                <br />
                <strong>이메일:</strong> mission@sungbok.church
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
