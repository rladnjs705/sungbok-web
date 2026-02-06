export function LocationSection() {
  return (
    <section id="directions" className="mt-16 scroll-mt-24">
      <article className="bg-white rounded-2xl p-8 md:p-12 shadow-md border border-gray-200">
        <h2 className="text-3xl font-bold text-primary-600 mb-8 pb-4 border-b-2 border-gray-200">
          오시는 길
        </h2>

        {/* Contact Info */}
        <div className="space-y-2 text-lg text-gray-700 mb-8">
          <p>
            <strong>주소:</strong> 서울특별시 강남구 테헤란로 123 성복교회
          </p>
          <p>
            <strong>전화:</strong> 02-1234-5678
          </p>
          <p>
            <strong>팩스:</strong> 02-1234-5679
          </p>
          <p>
            <strong>이메일:</strong> info@sungbok.church
          </p>
        </div>

        {/* Map Placeholder */}
        <div className="w-full h-96 bg-gray-100 rounded-xl flex items-center justify-center text-6xl mb-8">
          🗺️
        </div>

        {/* Public Transportation */}
        <div className="space-y-6">
          <div>
            <h3 className="text-xl font-bold text-gray-900 mb-3">
              대중교통 이용
            </h3>
            <p className="text-gray-700 leading-relaxed">
              <strong>지하철:</strong> 2호선 강남역 3번 출구 도보 10분
              <br />
              <strong>버스:</strong> 146, 401, 472번 강남역 하차
            </p>
          </div>

          <div>
            <h3 className="text-xl font-bold text-gray-900 mb-3">주차 안내</h3>
            <p className="text-gray-700 leading-relaxed">
              교회 지하 1-3층 주차장 이용 가능 (200대 수용)
              <br />
              주일 예배 시간에는 주차 공간이 부족할 수 있으니 대중교통 이용을
              권장합니다.
            </p>
          </div>
        </div>
      </article>
    </section>
  );
}
