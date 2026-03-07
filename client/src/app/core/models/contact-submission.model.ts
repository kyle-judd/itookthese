export interface ContactSubmission {
  id: number;
  name: string;
  subject: string;
  email: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}
