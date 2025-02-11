'use client';

import { useEffect, useState } from 'react';
import { privateApi } from '@/api/axios';
import { useRouter } from 'next/navigation';

interface Applicant {
  userId: number;
  status: string;
  userProfile: {
    name: string;
    email: string;
    introduction: string;
    job: string;
    jobSkills: Array<{ name: string; code: string }>;
  };
}

export default function ApplicationManagement({ params }: { params: { postId: string } }) {
  const router = useRouter();
  const [applicants, setApplicants] = useState<Applicant[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchApplicants();
  }, [params.postId]);

  const fetchApplicants = async () => {
    try {
      const response = await privateApi.get(`/api/v1/recruitment/${params.postId}/applied-users`);
      setApplicants(response.data.data.recruitmentUserList.content);
    } catch (error) {
      console.error('Failed to fetch applicants:', error);
      alert('신청자 목록을 불러오는데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleAccept = async (userId: number) => {
    try {
      await privateApi.patch(`/api/v1/recruitment/${params.postId}/accept`, { userId });
      alert('신청이 승인되었습니다.');
      fetchApplicants();
    } catch (error) {
      alert('승인 처리에 실패했습니다.');
    }
  };

  const handleReject = async (userId: number) => {
    try {
      await privateApi.patch(`/api/v1/recruitment/${params.postId}/reject`, { userId });
      alert('신청이 거절되었습니다.');
      fetchApplicants();
    } catch (error) {
      alert('거절 처리에 실패했습니다.');
    }
  };

  if (isLoading) return <div>로딩중...</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">모집 신청자 관리</h1>
      
      <div className="space-y-4">
        {applicants.length > 0 ? (
          applicants.map((applicant) => (
            <div key={applicant.userId} className="border rounded-lg p-4 bg-white">
              <div className="flex justify-between items-start">
                <div>
                  <h2 className="text-xl font-semibold">{applicant.userProfile.name}</h2>
                  <p className="text-gray-600">{applicant.userProfile.email}</p>
                  <p className="text-gray-700 mt-2">{applicant.userProfile.job}</p>
                  <div className="mt-2 flex flex-wrap gap-2">
                    {applicant.userProfile.jobSkills?.map((skill) => (
                      <span key={skill.code} className="bg-gray-100 px-2 py-1 rounded text-sm">
                        {skill.name}
                      </span>
                    ))}
                  </div>
                  <p className="mt-3 text-gray-700">{applicant.userProfile.introduction}</p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => handleAccept(applicant.userId)}
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                  >
                    승인
                  </button>
                  <button
                    onClick={() => handleReject(applicant.userId)}
                    className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                  >
                    거절
                  </button>
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="text-center py-8 text-gray-500">
            아직 신청자가 없습니다.
          </div>
        )}
      </div>
    </div>
  );
}