/**
 * File Upload Component
 * 
 * 파일 선택, 검증, 업로드 진행률 표시를 처리하는 컴포넌트입니다.
 * shadcn/ui 컴포넌트를 사용합니다.
 */

'use client';

import * as React from 'react';
import { useCallback, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Progress } from '@/components/ui/progress';
import { useFileUpload } from '@/hooks/useFileUpload';
import { UploadStatus } from '@/types/upload';
import { cn } from '@/lib/utils';

const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

/**
 * 파일 크기를 포맷팅합니다.
 * @param bytes - 바이트 단위 크기
 * @returns 포맷팅된 문자열 (예: "1.5 MB")
 */
function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B';
  const units = ['B', 'KB', 'MB', 'GB'];
  const k = 1024;
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  const size = bytes / Math.pow(k, i);
  return `${size.toFixed(i === 0 ? 0 : 2)} ${units[i]}`;
}

/**
 * 업로드 상태에 따른 메시지를 반환합니다.
 */
function getStatusMessage(status: UploadStatus): string {
  switch (status) {
    case 'idle':
      return '파일을 선택해주세요.';
    case 'calculating':
      return '체크섬을 계산하는 중...';
    case 'presigning':
      return '업로드 URL을 요청하는 중...';
    case 'uploading':
      return '업로드 중...';
    case 'completing':
      return '업로드를 완료하는 중...';
    case 'success':
      return '업로드가 완료되었습니다!';
    case 'error':
      return '업로드 중 에러가 발생했습니다.';
    default:
      return '';
  }
}

/**
 * 업로드 상태에 따른 색상을 반환합니다.
 */
function getStatusColor(status: UploadStatus): string {
  switch (status) {
    case 'success':
      return 'text-green-600';
    case 'error':
      return 'text-red-600';
    case 'calculating':
    case 'presigning':
    case 'uploading':
    case 'completing':
      return 'text-blue-600';
    default:
      return 'text-neutral-500';
  }
}

export interface FileUploadProps {
  /** 업로드 폴터 경로 (예: "notices/2024/03") */
  folder: string;
  /** 업로드 성공 시 호출되는 콜백 */
  onSuccess?: (url: string) => void;
  /** 업로드 실패 시 호출되는 콜백 */
  onError?: (error: string) => void;
  /** 추가 CSS 클래스 */
  className?: string;
  /** 버튼 텍스트 */
  buttonText?: string;
  /** 허용할 파일 타입 (예: "image/*,application/pdf") */
  accept?: string;
  /** 인증 토큰 */
  token: string;
}

/**
 * 파일 업로드 컴포넌트
 */
export function FileUpload({
  folder,
  onSuccess,
  onError,
  className,
  buttonText = '파일 선택',
  accept,
  token,
}: FileUploadProps) {
  const { upload, progress, isUploading, status, error, reset } = useFileUpload();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const fileInputRef = React.useRef<HTMLInputElement>(null);

  const handleFileChange = useCallback(
    (event: React.ChangeEvent<HTMLInputElement>) => {
      const file = event.target.files?.[0];
      if (file) {
        // 파일 크기 검증
        if (file.size > MAX_FILE_SIZE) {
          onError?.(`파일 크기가 너무 큽니다. (최대 ${formatFileSize(MAX_FILE_SIZE)})`);
          return;
        }
        setSelectedFile(file);
        reset();
      }
    },
    [onError, reset]
  );

  const handleUpload = useCallback(async () => {
    if (!selectedFile) return;

    try {
      const result = await upload(selectedFile, folder, token);
      onSuccess?.(result.url);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '업로드에 실패했습니다.';
      onError?.(errorMessage);
    }
  }, [selectedFile, folder, token, upload, onSuccess, onError]);

  const handleReset = useCallback(() => {
    setSelectedFile(null);
    reset();
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  }, [reset]);

  return (
    <div className={cn('w-full space-y-4', className)}>
      {/* 파일 선택 영역 */}
      <div className="flex items-center gap-3">
        <Input
          ref={fileInputRef}
          type="file"
          onChange={handleFileChange}
          accept={accept}
          disabled={isUploading}
          className="cursor-pointer file:mr-4 file:rounded-lg file:border-0 file:bg-primary-50 file:px-4 file:py-2 file:text-sm file:font-medium file:text-primary-700 hover:file:bg-primary-100"
        />
      </div>

      {/* 선택된 파일 정보 */}
      {selectedFile && (
        <div className="rounded-lg border border-neutral-200 bg-neutral-50 p-3 dark:border-neutral-800 dark:bg-neutral-900">
          <p className="text-sm font-medium text-neutral-900 dark:text-neutral-100">
            {selectedFile.name}
          </p>
          <p className="text-xs text-neutral-500">{formatFileSize(selectedFile.size)}</p>
        </div>
      )}

      {/* 업로드 버튼 */}
      <div className="flex gap-2">
        <Button
          onClick={handleUpload}
          disabled={!selectedFile || isUploading}
          className="flex-1"
        >
          {isUploading ? '업로드 중...' : buttonText}
        </Button>
        {(selectedFile || status === 'success' || status === 'error') && (
          <Button variant="outline" onClick={handleReset} disabled={isUploading}>
            초기화
          </Button>
        )}
      </div>

      {/* 진행률 표시 */}
      {(isUploading || status === 'success') && (
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs text-neutral-500">
              {formatFileSize(progress.loaded)} / {formatFileSize(progress.total)}
            </span>
            <span className="text-xs font-medium text-neutral-700">
              {progress.percentage}%
            </span>
          </div>
          <Progress value={progress.percentage} size="lg" />
        </div>
      )}

      {/* 상태 메시지 */}
      <p className={cn('text-sm', getStatusColor(status))}>
        {getStatusMessage(status)}
      </p>

      {/* 에러 메시지 */}
      {error && (
        <div className="rounded-lg border border-red-200 bg-red-50 p-3 dark:border-red-900 dark:bg-red-950">
          <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
        </div>
      )}

      {/* 성공 메시지 */}
      {status === 'success' && !error && (
        <div className="rounded-lg border border-green-200 bg-green-50 p-3 dark:border-green-900 dark:bg-green-950">
          <p className="text-sm text-green-600 dark:text-green-400">
            파일이 성공적으로 업로드되었습니다!
          </p>
        </div>
      )}
    </div>
  );
}
