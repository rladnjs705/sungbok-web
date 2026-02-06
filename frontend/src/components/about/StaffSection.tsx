import Image from 'next/image';

const SENIOR_PASTORS = [
  {
    id: 1,
    name: '이태희 원로목사',
    title: 'Senior Pastor Emeritus',
    image: '/images/pastor_chairman.jpg',
    description:
      '성복교회를 개척하여 오랜 기간 교회를 섬기신 원로목사님이십니다. 현재도 교회를 위해 기도하고 계십니다.',
  },
  {
    id: 2,
    name: '이요셉 담임목사',
    title: 'Senior Pastor',
    image: '/images/pastor_senior.jpg',
    description:
      '성복교회 담임목사로 교회를 섬기고 있습니다. 말씀 중심의 목회를 통해 성도들의 영적 성장을 돕고, 다음 세대를 세우는 일에 힘쓰고 있습니다.',
  },
] as const;

const STAFF_MEMBERS = [
  { name: '이원효 목사', role: '수석목사/3교구', image: '/images/pro_c01.jpg' },
  { name: '조정한 목사', role: '4교구/미디어', image: '/images/pro_c02.jpg' },
  { name: '김정한 목사', role: '교육부 총괄/청년부', image: '/images/pro_c03.jpg' },
  { name: '황창조 목사', role: '2교구/초등부', image: '/images/pro_c04.jpg' },
  { name: '박건호 목사', role: '5/6교구', image: '/images/pro_c05.jpg' },
  { name: '김민기 목사', role: '1교구/중등부', image: '/images/pro_c06.jpg' },
  { name: '임경일 목사', role: '찬양 디렉터/엘림가족부', image: '/images/pro_c07.jpg' },
  { name: '최미정 전도사', role: '1-6교구', image: '/images/pro_c08.jpg' },
  { name: '최주찬 전도사', role: '고등부/영어예배부', image: '/images/pro_c09.jpg' },
  { name: '신미자 간사', role: '1교구', image: '/images/pro_c10.jpg' },
  { name: '이옥근 간사', role: '2교구', image: '/images/pro_c11.jpg' },
  { name: '양영복 간사', role: '4교구', image: '/images/pro_c12.jpg' },
  { name: '심흥숙 간사', role: '5교구', image: '/images/pro_c13.jpg' },
  { name: '이진희 간사', role: '영아부', image: '/images/pro_c14.jpg' },
  { name: '이명화 간사', role: '유치부', image: '/images/pro_c15.jpg' },
  { name: '천지연 간사', role: '유년부', image: '/images/pro_c16.jpg' },
] as const;

export function StaffSection() {
  return (
    <section className="mt-16">
      <article className="bg-white rounded-2xl p-8 md:p-12 shadow-md border border-gray-200">
        <h2 className="text-3xl font-bold text-primary-600 mb-8 pb-4 border-b-2 border-gray-200">
          섬기는 이들
        </h2>

        {/* Senior Pastors */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-12">
          {SENIOR_PASTORS.map((pastor) => (
            <div
              key={pastor.id}
              className="bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-lg transition-shadow"
            >
              <div className="relative h-96">
                <Image
                  src={pastor.image}
                  alt={pastor.name}
                  fill
                  className="object-cover"
                />
              </div>
              <div className="p-6">
                <p className="text-sm text-primary-600 font-semibold tracking-wider uppercase mb-2">
                  {pastor.title}
                </p>
                <h3 className="text-2xl font-bold text-gray-900 mb-4">
                  {pastor.name}
                </h3>
                <p className="text-gray-700 leading-relaxed">
                  {pastor.description}
                </p>
              </div>
            </div>
          ))}
        </div>

        {/* Staff Members */}
        <h3 className="text-2xl font-bold text-gray-900 mb-6">
          부목사 및 전도사
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {STAFF_MEMBERS.map((member, index) => (
            <div key={index} className="text-center">
              <div className="relative aspect-[3/4] mb-3 rounded-lg overflow-hidden">
                <Image
                  src={member.image}
                  alt={member.name}
                  fill
                  className="object-cover"
                />
              </div>
              <h4 className="text-lg font-bold text-gray-900 mb-1">
                {member.name}
              </h4>
              <p className="text-sm text-gray-600">{member.role}</p>
            </div>
          ))}
        </div>
      </article>
    </section>
  );
}
