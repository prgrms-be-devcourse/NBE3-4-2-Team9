'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { getJobPostingDetail } from '@/api/jobposting';
import { JobPostingDetail } from '@/types/jobposting';
import { formatDate } from '@/utils/dateUtils';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';

export default function JobPostingDetail() {
  const params = useParams();
  const router = useRouter();
  const [posting, setPosting] = useState<JobPostingDetail | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchJobPosting = async () => {
      try {
        const response = await getJobPostingDetail(Number(params.id));
        if (response.success) {
          setPosting(response.data);
        }
      } catch (error) {
        console.error('Failed to fetch job posting:', error);
        alert('채용공고를 불러오는데 실패했습니다.');
        router.push('/job-postings');
      } finally {
        setIsLoading(false);
      }
    };

    if (params.id) {
      fetchJobPosting();
    }
  }, [params.id, router]);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!posting) {
    return <div>채용공고를 찾을 수 없습니다.</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4">
        <div className="mb-6">
          <button
            onClick={() => router.back()}
            className="flex items-center text-gray-600 hover:text-gray-900"
          >
            <ArrowLeftIcon className="h-5 w-5 mr-2" />
            목록으로
          </button>
        </div>

        <div className="bg-white rounded-xl shadow-lg overflow-hidden">
          <div className="p-8">
            <h1 className="text-3xl font-bold mb-6">{posting.subject}</h1>
            
            <div className="grid grid-cols-2 gap-6 mb-8">
              <div>
                <h2 className="text-lg font-semibold mb-2">기본 정보</h2>
                <div className="space-y-2 text-gray-600">
                  <p>회사: {posting.companyName}</p>
                  <p>경력: {posting.experienceLevel.name}</p>
                  <p>학력: {posting.requireEducate.name}</p>
                  <p>연봉: {posting.salary.name}</p>
                </div>
              </div>
              
              <div>
                <h2 className="text-lg font-semibold mb-2">기간 정보</h2>
                <div className="space-y-2 text-gray-600">
                  <p>시작일: {formatDate(posting.openDate)}</p>
                  <p>마감일: {formatDate(posting.closeDate)}</p>
                  <p>게시일: {formatDate(posting.postDate)}</p>
                </div>
              </div>
            </div>

            <div className="mb-8">
              <h2 className="text-lg font-semibold mb-2">필요 기술</h2>
              <div className="flex flex-wrap gap-2">
                {posting.jobSkillList.map((skill) => (
                  <span 
                    key={skill.code}
                    className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm"
                  >
                    {skill.name}
                  </span>
                ))}
              </div>
            </div>

            <div className="flex justify-between items-center">
              <a
                href={posting.url}
                target="_blank"
                rel="noopener noreferrer"
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                지원하기
              </a>
              <div className="flex items-center gap-4">
                <span className="text-gray-600">지원자 {posting.applyCnt}명</span>
                <span className={`px-3 py-1 rounded ${
                  posting.jobPostingStatus === 'ACTIVE' 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {posting.jobPostingStatus === 'ACTIVE' ? '진행중' : '마감'}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 