// 장년예배 일정
const ADULT_WORSHIP = [
  {
    id: 'sunday-worship',
    title: '주일예배',
    schedules: [
      { name: '주일 1부', time: '오전 7:00', location: '예루살렘성전[3F]' },
      { name: '주일 2부', time: '오전 9:00', location: '예루살렘성전[3F]' },
      { name: '주일 3부', time: '오전 11:00', location: '예루살렘성전[3F]' },
      { name: '주일 4부', time: '오후 1:00', location: '예루살렘성전[3F]' },
      { name: '주일 5부', time: '오후 3:00', location: '예루살렘성전[3F]' },
    ],
  },
  {
    id: 'dawn-prayer',
    title: '새벽예배(월~금)',
    schedules: [{ time: '오전 5:30', location: '임마누엘성전[3F]' }],
  },
  {
    id: 'wednesday-worship',
    title: '수요예배',
    schedules: [{ time: '오후 7:20', location: '임마누엘성전[3F]' }],
  },
  {
    id: 'friday-prayer',
    title: '금요성수의장',
    schedules: [{ time: '오후 8:30', location: '예루살렘성전[3F]' }],
  },
] as const;

// 다음세대예배 일정
const NEXT_GENERATION_WORSHIP = [
  {
    id: 'infant',
    title: '영아부',
    time: '주일 오전 11:00',
    location: '사랑실[2F]',
  },
  {
    id: 'kindergarten',
    title: '유치부',
    time: '주일 오전 11:00',
    location: '믿음실[2F]',
  },
  {
    id: 'elementary-lower',
    title: '유년부',
    time: '주일 오전 11:00',
    location: '소망실[2F]',
  },
  {
    id: 'elementary',
    title: '초등부',
    time: '주일 오전 11:00',
    location: '평화실[3F]',
  },
  {
    id: 'middle-school',
    title: '중등부',
    time: '주일 오전 11:00',
    location: '평화실선[3F]',
  },
  {
    id: 'high-school',
    title: '고등부',
    time: '주일 오전 9:00',
    location: '평화실선[3F]',
  },
  {
    id: 'english',
    title: '영어예배',
    time: '주일 오후 1:20',
    location: '평화실선[4F]',
  },
  {
    id: 'youth',
    title: '청년부',
    schedules: [
      { name: '화요기도모임', time: '오후 8:00', location: '평화실선[4F]' },
      { name: '청년소모임', time: '오후 8:30', location: '평화실선[4F]' },
      { name: '주일', time: '오전 1:00', location: '예루살렘성전[3F]' },
    ],
  },
  {
    id: 'young-couples',
    title: '젊은부부',
    time: '주일 오후 2:40',
    location: '사랑실[2F]',
  },
] as const;

export function WorshipSchedule() {
  return (
    <section>
      <h2 className="text-3xl font-bold text-center text-gray-900 dark:text-white mb-12">
        예배 시간표
      </h2>

      {/* 장년예배 */}
      <div className="mb-16">
        <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
          장년예배
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          {ADULT_WORSHIP.map((worship) => (
            <div
              key={worship.id}
              className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-6 hover:border-primary-500 dark:hover:border-primary-400 hover:shadow-lg transition-all"
            >
              <h4 className="text-lg font-bold text-primary-600 dark:text-primary-400 mb-4 text-center">
                {worship.title}
              </h4>
              <div className="space-y-2">
                {worship.schedules.map((schedule, idx) => (
                  <div key={idx} className="text-sm">
                    {schedule.name && (
                      <p className="font-semibold text-gray-900 dark:text-gray-100">
                        • {schedule.name}
                      </p>
                    )}
                    <p className="text-gray-700 dark:text-gray-300 ml-3">
                      {schedule.time} {schedule.location}
                    </p>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 다음세대예배 */}
      <div>
        <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
          다음세대예배
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          {NEXT_GENERATION_WORSHIP.map((worship) => (
            <div
              key={worship.id}
              className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-6 hover:border-primary-500 dark:hover:border-primary-400 hover:shadow-lg transition-all"
            >
              <h4 className="text-lg font-bold text-primary-600 dark:text-primary-400 mb-4 text-center">
                {worship.title}
              </h4>
              {worship.schedules ? (
                <div className="space-y-2">
                  {worship.schedules.map((schedule, idx) => (
                    <div key={idx} className="text-sm">
                      {schedule.name && (
                        <p className="font-semibold text-gray-900 dark:text-gray-100">
                          • {schedule.name}
                        </p>
                      )}
                      <p className="text-gray-700 dark:text-gray-300 ml-3">
                        {schedule.time} {schedule.location}
                      </p>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-sm text-center">
                  <p className="text-gray-700 dark:text-gray-300 mb-1">
                    {worship.time}
                  </p>
                  <p className="text-gray-600 dark:text-gray-400 text-xs">
                    {worship.location}
                  </p>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
