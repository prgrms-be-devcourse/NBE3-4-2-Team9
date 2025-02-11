'use client';

import { useSelector } from 'react-redux';
import { useRouter } from 'next/navigation';
import { RootState } from '@/store/store';
import { useEffect, useState } from 'react';
import Link from 'next/link';
import { ChatBubbleLeftIcon, UserGroupIcon, ClipboardDocumentListIcon } from '@heroicons/react/24/outline';

interface UserProfile {
  id: number;
  email: string;
  name: string;
  profileImg?: string;
  introduction?: string;
  job?: string;
  jobSkills?: Array<{
    name: string;
    code: number;
  }>;
  posts?: Array<{
    postId: number;
    subject: string;
    createdAt: string;
    categoryName: string;
  }>;
  comments?: Array<{
    commentId: number;
    postId: number;
    postSubject: string;
    content: string;
    createdAt: string;
  }>;
}

export default function UserProfile({ params }: { params: { id: string } }) {
  const router = useRouter();
  const { isAuthenticated, token, user } = useSelector((state: RootState) => state.auth);
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const [myPosts, setMyPosts] = useState([]); // 추후 API 연동 시 사용

  useEffect(() => {
    if (!isAuthenticated || !user) {
      router.push('/login');
      return;
    }

    // 관리자는 admin/profile로 리다이렉트
    if (user.email?.includes('admin')) {
      router.push('/admin/profile');
      return;
    }

    // 일반 유저는 id 검증
    if (Number(user.id) !== Number(params.id)) {
      router.push('/');
      return;
    }

    fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/users/${params.id}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    })
    .then(async res => {
      const data = await res.json();
      if (!res.ok || !data.success) {
        throw new Error(data.message || 'Failed to fetch profile');
      }
      setProfile(data.data);
    })
    .catch(error => {
      console.error('Failed to fetch profile:', error);
      router.push('/');
    });
  }, [isAuthenticated, user, params.id, router, token]);

  const handleLogout = () => {
    // 로그아웃 로직 구현
  };

  if (!profile) {
    return <div>Loading...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-12">
      <div className="max-w-3xl mx-auto">
        <div className="bg-white shadow-md rounded-xl overflow-hidden">
          {/* 프로필 헤더 배경 */}
          <div className="h-32 bg-gradient-to-r from-primary/20 to-primary/10" />
          
          <div className="px-8 pb-8">
            {/* 프로필 상단 영역 */}
            <div className="flex justify-between relative -mt-16 mb-8">
              <div className="flex items-end space-x-5">
                {/* 프로필 이미지와 드롭다운 */}
                <div className="relative">
                  <div className="w-24 h-24 rounded-xl overflow-hidden ring-4 ring-white shadow-lg">
                    <img
                      src={profile.profileImg || '/default-profile.png'}
                      alt={profile.name}
                      className="w-full h-full object-cover"
                    />
                  </div>
                </div>
                
                <div className="pb-1">
                  <h1 className="text-2xl font-bold text-gray-900">{profile.name}</h1>
                  <p className="text-gray-500">{profile.email}</p>
                </div>
              </div>
            </div>

            {/* 프로필 정보 영역 */}
            <div className="grid gap-8">
              <div className="p-6 bg-gray-50 rounded-xl">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">직업</h2>
                <p className="text-gray-700">
                  {profile.job || '등록된 직업이 없습니다.'}
                </p>
              </div>
              
              <div className="p-6 bg-gray-50 rounded-xl">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">소개</h2>
                <p className="text-gray-700 whitespace-pre-line">
                  {profile.introduction || '등록된 소개가 없습니다.'}
                </p>
              </div>
              
              <div className="p-6 bg-gray-50 rounded-xl">
                <h2 className="text-lg font-semibold text-gray-900 mb-4">보유 기술</h2>
                {profile.jobSkills && profile.jobSkills.length > 0 ? (
                  <div className="flex flex-wrap gap-2">
                    {profile.jobSkills.map((skill) => (
                      <span 
                        key={skill.code}
                        className="px-4 py-1.5 bg-white text-gray-700 rounded-lg text-sm border border-gray-200
                                 shadow-sm hover:shadow transition-shadow duration-200"
                      >
                        {skill.name}
                      </span>
                    ))}
                  </div>
                ) : (
                  <p className="text-gray-700">등록된 기술이 없습니다.</p>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* 내가 작성한 게시글 섹션 */}
        <div className="bg-white rounded-xl shadow-lg p-6 mt-8">
          <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
            <ChatBubbleLeftIcon className="h-6 w-6 text-blue-600" />
            내가 작성한 게시글
          </h3>
          
          {profile.posts && profile.posts.length > 0 ? (
            <div className="space-y-4">
              {profile.posts.map((post) => (
                <div key={post.postId} className="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                  <Link href={`/posts/${post.postId}`} className="block">
                    <div className="flex justify-between items-center">
                      <h4 className="text-lg font-medium text-gray-900">{post.subject}</h4>
                      <span className="text-sm text-gray-500">
                        {new Date(post.createdAt).toLocaleDateString('ko-KR')}
                      </span>
                    </div>
                    <p className="text-sm text-gray-600 mt-1">
                      카테고리: {post.categoryName}
                    </p>
                  </Link>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <div className="bg-gray-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                <ChatBubbleLeftIcon className="h-8 w-8 text-gray-400" />
              </div>
              <p className="text-gray-500 mb-4">아직 작성한 게시글이 없습니다</p>
              <Link
                href="/post/write"
                className="inline-flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
              >
                첫 게시글 작성하기
              </Link>
            </div>
          )}
        </div>

        {/* 내가 작성한 댓글 섹션 */}
        <div className="bg-white rounded-xl shadow-lg p-6 mt-8">
          <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
            <ChatBubbleLeftIcon className="h-6 w-6 text-blue-600" />
            내가 작성한 댓글
          </h3>
          
          {profile.comments && profile.comments.length > 0 ? (
            <div className="space-y-4">
              {profile.comments.map((comment) => (
                <div key={comment.commentId} className="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                  <Link href={`/posts/${comment.postId}`} className="block">
                    <div className="flex justify-between items-center">
                      <h4 className="text-sm font-medium text-gray-600">
                        게시글: {comment.postSubject}
                      </h4>
                      <span className="text-sm text-gray-500">
                        {new Date(comment.createdAt).toLocaleDateString('ko-KR')}
                      </span>
                    </div>
                    <p className="text-gray-900 mt-2">{comment.content}</p>
                  </Link>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <div className="bg-gray-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                <ChatBubbleLeftIcon className="h-8 w-8 text-gray-400" />
              </div>
              <p className="text-gray-500">아직 작성한 댓글이 없습니다</p>
            </div>
          )}
        </div>

        {/* 모집자 명단 섹션 */}
        <div className="bg-white rounded-xl shadow-lg p-6 mt-8">
          <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
            <UserGroupIcon className="h-6 w-6 text-blue-600" />
            모집자 명단
          </h3>
          
          <div className="text-center py-12">
            <div className="bg-gray-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
              <UserGroupIcon className="h-8 w-8 text-gray-400" />
            </div>
            <p className="text-gray-500 mb-4">아직 모집 중인 프로젝트가 없습니다</p>
          </div>
        </div>

        {/* 모집 신청 리스트 섹션 */}
        <div className="bg-white rounded-xl shadow-lg p-6 mt-8">
          <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
            <ClipboardDocumentListIcon className="h-6 w-6 text-blue-600" />
            모집 신청 리스트
          </h3>
          
          <div className="text-center py-12">
            <div className="bg-gray-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
              <ClipboardDocumentListIcon className="h-8 w-8 text-gray-400" />
            </div>
            <p className="text-gray-500 mb-4">아직 신청한 프로젝트가 없습니다</p>
          </div>
        </div>
      </div>
    </div>
  );
} 