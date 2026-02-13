import { NextRequest, NextResponse } from "next/server";

/**
 * 업로드 완료 확인 API
 * POST /api/upload/{uploadId}/complete
 *
 * 동적 라우트 파라미터:
 * - uploadId: 업로드 ID (숫자)
 *
 * 요청 본문:
 * {
 *   key: string - 저장된 파일 키
 *   checksum: string - SHA-256 해시값
 * }
 *
 * 응답:
 * {
 *   success: boolean - 업로드 완료 여부
 *   fileUrl: string - 공개 접근 가능한 파일 URL
 * }
 */
export async function POST(
  request: NextRequest,
  { params }: { params: Promise<{ uploadId: string }> }
) {
  try {
    // 동적 라우트 파라미터 추출
    const { uploadId } = await params;

    // uploadId 검증
    const uploadIdNum = parseInt(uploadId, 10);
    if (isNaN(uploadIdNum) || uploadIdNum <= 0) {
      return NextResponse.json(
        {
          error: "Bad Request",
          message: "Invalid uploadId: must be a positive number",
        },
        { status: 400 }
      );
    }

    // 요청 본문 파싱
    const body = await request.json();
    const { key, checksum } = body;

    // 필수 필드 검증
    if (!key || !checksum) {
      return NextResponse.json(
        {
          error: "Bad Request",
          message: "Missing required fields: key, checksum",
        },
        { status: 400 }
      );
    }

    // Authorization 헤더 추출
    const authHeader = request.headers.get("Authorization");
    if (!authHeader) {
      return NextResponse.json(
        {
          error: "Unauthorized",
          message: "Authorization header is required",
        },
        { status: 401 }
      );
    }

    // 백엔드 API URL
    const apiUrl = process.env.NEXT_PUBLIC_API_URL;
    if (!apiUrl) {
      console.error("NEXT_PUBLIC_API_URL is not defined");
      return NextResponse.json(
        {
          error: "Internal Server Error",
          message: "Server configuration error",
        },
        { status: 500 }
      );
    }

    // 백엔드 API 호출
    const backendResponse = await fetch(
      `${apiUrl}/uploads/${uploadId}/complete`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: authHeader,
        },
        body: JSON.stringify({
          key,
          checksum,
        }),
      }
    );

    // 백엔드 응답 처리
    if (!backendResponse.ok) {
      const errorData = await backendResponse.json().catch(() => ({
        error: "Unknown Error",
        message: `Backend returned status ${backendResponse.status}`,
      }));

      return NextResponse.json(errorData, {
        status: backendResponse.status,
      });
    }

    // 백엔드 응답 반환
    const data = await backendResponse.json();
    return NextResponse.json(data, { status: 200 });
  } catch (error) {
    console.error("Upload Complete API Error:", error);

    // 네트워크 에러 처리
    if (error instanceof TypeError && error.message.includes("fetch")) {
      return NextResponse.json(
        {
          error: "Service Unavailable",
          message: "Failed to connect to backend server",
        },
        { status: 503 }
      );
    }

    return NextResponse.json(
      {
        error: "Internal Server Error",
        message: error instanceof Error ? error.message : "Unknown error occurred",
      },
      { status: 500 }
    );
  }
}
