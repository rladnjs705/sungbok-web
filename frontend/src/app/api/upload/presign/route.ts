import { NextRequest, NextResponse } from "next/server";

/**
 * Pre-signed URL 요청 API
 * POST /api/upload/presign
 *
 * 요청 본문:
 * {
 *   filename: string - 원본 파일명
 *   folder: string - 업로드 폴더 (예: "notices", "galleries")
 *   contentType: string - MIME 타입 (예: "application/pdf")
 *   fileSize: number - 파일 크기 (바이트)
 *   checksum: string - SHA-256 해시값
 * }
 *
 * 응답:
 * {
 *   uploadId: number - 업로드 ID
 *   url: string - Pre-signed URL
 *   key: string - 저장될 파일 키
 *   expiresAt: string - URL 만료 시간 (ISO 8601)
 * }
 */
export async function POST(request: NextRequest) {
  try {
    // 요청 본문 파싱
    const body = await request.json();
    const { filename, folder, contentType, fileSize, checksum } = body;

    // 필수 필드 검증
    if (!filename || !folder || !contentType || !fileSize || !checksum) {
      return NextResponse.json(
        {
          error: "Bad Request",
          message: "Missing required fields: filename, folder, contentType, fileSize, checksum",
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
    const backendResponse = await fetch(`${apiUrl}/uploads/presign`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: authHeader,
      },
      body: JSON.stringify({
        filename,
        folder,
        contentType,
        fileSize,
        checksum,
      }),
    });

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
    console.error("Presign API Error:", error);

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
