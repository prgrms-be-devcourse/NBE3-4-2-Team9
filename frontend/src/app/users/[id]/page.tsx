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
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 왼쪽 사이드바: 프로필 정보 */}
        <div className="lg:col-span-1 space-y-6">
          <div className="bg-white shadow-md rounded-xl overflow-hidden">
            {/* 프로필 헤더 */}
            <div className="h-32 bg-gradient-to-r from-primary/20 to-primary/10" />
            
            <div className="px-6 pb-6">
              {/* 프로필 이미지와 기본 정보 */}
              <div className="relative -mt-16 mb-4">
                <div className="w-24 h-24 rounded-xl overflow-hidden ring-4 ring-white shadow-lg">
                  <img
                    src={profile.profileImg || '/default-profile.png'}
                    alt={profile.name}
                    className="w-full h-full object-cover"
                  />
                </div>
              </div>
              
              <div className="space-y-2">
                <h1 className="text-2xl font-bold text-gray-900">{profile.name}</h1>
                <p className="text-gray-500">{profile.email}</p>
              </div>
            </div>
          </div>

          {/* 직업 정보 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">직업</h2>
            <p className="text-gray-700">
              {profile.job || '등록된 직업이 없습니다.'}
            </p>
          </div>

          {/* 소개 정보 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">소개</h2>
            <p className="text-gray-700 whitespace-pre-line">
              {profile.introduction || '등록된 소개가 없습니다.'}
            </p>
          </div>

          {/* 보유 기술 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">보유 기술</h2>
            {profile.jobSkills && profile.jobSkills.length > 0 ? (
              <div className="flex flex-wrap gap-2">
                {profile.jobSkills.map((skill) => (
                  <span 
                    key={skill.code}
                    className="px-3 py-1 bg-gray-100 text-gray-700 rounded-lg text-sm"
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

        {/* 오른쪽 메인 컨텐츠: 게시글, 댓글, 모집 정보 */}
        <div className="lg:col-span-2 space-y-6">
          {/* 내가 작성한 게시글 섹션 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold flex items-center gap-2">
                <ChatBubbleLeftIcon className="h-6 w-6 text-blue-600" />
                내가 작성한 게시글
              </h3>
              {profile.posts && profile.posts.length > 3 && (
                <Link 
                  href={`/users/${profile.id}/posts`}
                  className="text-sm text-blue-600 hover:text-blue-800 font-medium"
                >
                  전체보기 ({profile.posts.length}개)
                </Link>
              )}
            </div>
            
            {profile.posts && profile.posts.length > 0 ? (
              <div className="divide-y divide-gray-100">
                {profile.posts.slice(0, 3).map((post) => (
                  <Link 
                    key={post.postId} 
                    href={`/posts/${post.postId}`} 
                    className="flex justify-between items-center py-3 hover:bg-gray-50 px-2 rounded transition-colors"
                  >
                    <div className="flex-1">
                      <h4 className="text-gray-900 font-medium">{post.subject}</h4>
                      <p className="text-sm text-gray-500 mt-1">{post.categoryName}</p>
                    </div>
                    <span className="text-sm text-gray-400 ml-4">
                      {new Date(post.createdAt).toLocaleDateString('ko-KR')}
                    </span>
                  </Link>
                ))}
              </div>
            ) : (
              <div className="text-center py-6">
                <p className="text-gray-500">아직 작성한 게시글이 없습니다</p>
              </div>
            )}
          </div>

          {/* 내가 작성한 댓글 섹션 */}
          <div className="bg-white shadow-md rounded-xl p-6">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold flex items-center gap-2">
                <ChatBubbleLeftIcon className="h-6 w-6 text-blue-600" />
                내가 작성한 댓글
              </h3>
              {profile.comments && profile.comments.length > 3 && (
                <Link 
                  href={`/users/${profile.id}/comments`}
                  className="text-sm text-blue-600 hover:text-blue-800 font-medium"
                >
                  전체보기 ({profile.comments.length}개)
                </Link>
              )}
            </div>
            
            {profile.comments && profile.comments.length > 0 ? (
              <div className="divide-y divide-gray-100">
                {profile.comments.slice(0, 3).map((comment) => (
                  <Link 
                    key={comment.commentId} 
                    href={`/posts/${comment.postId}`}
                    className="block py-3 hover:bg-gray-50 px-2 rounded transition-colors"
                  >
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">게시글: {comment.postSubject}</span>
                      <span className="text-sm text-gray-400">
                        {new Date(comment.createdAt).toLocaleDateString('ko-KR')}
                      </span>
                    </div>
                    <p className="text-gray-900 mt-2 line-clamp-1">{comment.content}</p>
                  </Link>
                ))}
              </div>
            ) : (
              <div className="text-center py-6">
                <p className="text-gray-500">아직 작성한 댓글이 없습니다</p>
              </div>
            )}
          </div>

          {/* 모집자 명단과 모집 신청 리스트 섹션들 */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-white shadow-md rounded-xl p-6">
              <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
                <UserGroupIcon className="h-6 w-6 text-blue-600" />
                모집자 명단
              </h3>
              <div className="text-center py-8">
                <div className="bg-gray-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                  <UserGroupIcon className="h-8 w-8 text-gray-400" />
                </div>
                <p className="text-gray-500">아직 모집 중인 프로젝트가 없습니다</p>
              </div>
            </div>

            <div className="bg-white shadow-md rounded-xl p-6">
              <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
                <ClipboardDocumentListIcon className="h-6 w-6 text-blue-600" />
                모집 신청 리스트
              </h3>
              <div className="text-center py-8">
                <div className="bg-gray-50 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-4">
                  <ClipboardDocumentListIcon className="h-8 w-8 text-gray-400" />
                </div>
                <p className="text-gray-500">아직 신청한 프로젝트가 없습니다</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
} 