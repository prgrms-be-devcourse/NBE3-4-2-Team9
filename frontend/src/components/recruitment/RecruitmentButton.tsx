'use client';

import { useState } from 'react';
import { privateApi } from '@/api/axios';
import { useRouter } from 'next/navigation';

interface RecruitmentButtonProps {
  postId: number;
  isAuthor: boolean;
  recruitmentStatus: string;
}

export default function RecruitmentButton({ postId, isAuthor, recruitmentStatus }: RecruitmentButtonProps) {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const handleApply = async () => {
    try {
      setIsLoading(true);
      await privateApi.post(`/api/v1/recruitment/${postId}`);
      alert('모집 신청이 완료되었습니다.');
      router.refresh();
    } catch (error: any) {
      alert(error.response?.data?.message || '모집 신청에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleManageApplications = () => {
    router.push(`/recruitment/${postId}/applications`);
  };

  if (isAuthor) {
    return (
      <button
        onClick={handleManageApplications}
        className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        disabled={isLoading}
      >
        신청자 관리
      </button>
    );
  }

  return (
    <button
      onClick={handleApply}
      className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:bg-gray-400"
      disabled={isLoading || recruitmentStatus === 'CLOSED'}
    >
      {recruitmentStatus === 'CLOSED' ? '모집 마감' : '모집 신청'}
    </button>
  );
} 