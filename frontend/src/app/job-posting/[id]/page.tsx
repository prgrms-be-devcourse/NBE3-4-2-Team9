'use client';

import { useEffect, useState } from 'react';
import { JobPostingPageResponse } from '@/types/job-posting/JobPostingPageResponse';
import { getJobPosting } from '@/api/jobPosting';
import { BriefcaseIcon, BuildingOfficeIcon, CalendarIcon, AcademicCapIcon } from '@heroicons/react/24/outline';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { privateApi } from '@/api/axios';

export default function JobPostingDetail({ params }: { params: { id: string } }) {
  const [posting, setPosting] = useState<JobPostingPageResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const fetchJobPosting = async () => {
      try {
        const response = await privateApi.get(`/api/v1/job-posting/${params.id}`);
        if (response.data.success) {
          setPosting(response.data.data);
        }
      } catch (error) {
        console.error('Failed to fetch job posting:', error);
        alert('채용공고를 불러오는데 실패했습니다.');
        router.push('/job-posting');
      } finally {
        setIsLoading(false);
      }
    };

    if (params.id) {
      fetchJobPosting();
    }
  }, [params.id]);

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto p-8 animate-pulse">
        <div className="h-8 bg-gray-200 rounded w-3/4 mb-6"></div>
        <div className="h-4 bg-gray-200 rounded w-1/2 mb-8"></div>
        <div className="space-y-4">
          <div className="h-4 bg-gray-200 rounded w-full"></div>
          <div className="h-4 bg-gray-200 rounded w-full"></div>
          <div className="h-4 bg-gray-200 rounded w-2/3"></div>
        </div>
      </div>
    );
  }

  if (!posting) {
    return (
      <div className="max-w-4xl mx-auto p-8">
        <p className="text-center text-gray-500">채용 공고를 찾을 수 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-8">
      <Link 
        href="/"
        className="inline-flex items-center text-blue-600 hover:text-blue-800 mb-6"
      >
        ← 목록으로 돌아가기
      </Link>

      <div className="bg-white rounded-lg shadow-lg p-8">
        <h1 className="text-3xl font-bold mb-4">{posting.subject}</h1>
        
        <div className="flex items-center text-gray-600 mb-6">
          <BuildingOfficeIcon className="h-5 w-5 mr-2" />
          <a 
            href={posting.companyLink} 
            target="_blank" 
            className="text-blue-600 hover:text-blue-800"
          >
            {posting.companyName}
          </a>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div className="flex items-center">
            <BriefcaseIcon className="h-5 w-5 text-gray-500 mr-2" />
            <span>
              경력: {posting.experienceLevel.name}
              {posting.experienceLevel.min > 0 && ` (${posting.experienceLevel.min}년 이상)`}
            </span>
          </div>
          
          <div className="flex items-center">
            <AcademicCapIcon className="h-5 w-5 text-gray-500 mr-2" />
            <span>학력: {posting.requireEducate.name}</span>
          </div>
          
          <div className="flex items-center">
            <CalendarIcon className="h-5 w-5 text-gray-500 mr-2" />
            <span>마감일: {new Date(posting.closeDate).toLocaleDateString()}</span>
          </div>
          
          <div className="flex items-center">
            <span className="font-semibold text-blue-600">{posting.salary.name}</span>
          </div>
        </div>

        <div className="mb-8">
          <h2 className="text-lg font-semibold mb-3">필요 기술</h2>
          <div className="flex flex-wrap gap-2">
            {posting.jobSkillList.map((skill) => (
              <span
                key={skill.jobSkillId}
                className="px-3 py-1 bg-blue-50 text-blue-700 rounded-full text-sm"
              >
                {skill.name}
              </span>
            ))}
          </div>
        </div>

        <a
          href={posting.url}
          target="_blank"
          className="inline-block w-full text-center bg-blue-600 text-white py-3 px-6 rounded-lg hover:bg-blue-700 transition-colors duration-200"
        >
          지원하기
        </a>
      </div>
    </div>
  );
} 