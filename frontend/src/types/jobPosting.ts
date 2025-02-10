export interface JobPosting {
  id: number;
  subject: string;
  openDate: string;
  closeDate: string;
  experienceLevel: {
    code: number;
    min: number;
    max: number;
    name: string;
  };
  requireEducate: {
    code: number;
    name: string;
  };
  jobPostingStatus: 'ACTIVE' | 'END';
  salary: {
    code: number;
    name: string;
  };
  applyCnt: number;
}

export interface JobPostingDetail extends JobPosting {
  url: string;
  postDate: string;
  companyName: string;
  companyLink: string;
  jobSkillList: Array<{
    name: string;
    code: number;
  }>;
  voterCount: number;
  isVoter: boolean;
}

export interface JobPostingSearchCondition {
  salaryCode?: number;
  kw?: string;
  experienceLevel?: number;
  requireEducateCode?: number;
  sort?: string;
  order?: string;
  pageNum?: number;
  pageSize?: number;
}

export interface JobPostingPageResponse {
  id: number;
  subject: string;
  openDate: string;
  closeDate: string;
  experienceLevel: {
    code: number;
    min: number;
    max: number;
    name: string;
  };
  requireEducate: {
    code: number;
    name: string;
  };
  jobPostingStatus: 'ACTIVE' | 'END';
  salary: {
    code: number;
    name: string;
  };
  applyCnt: number;
}

export interface JobPostingPage {
  content: JobPostingPageResponse[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  empty: boolean;
} 