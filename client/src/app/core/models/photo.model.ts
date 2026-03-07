export interface Photo {
  id: number;
  title: string;
  description: string | null;
  thumbUrl: string;
  placeholderBase64: string | null;
  mediumUrl: string;
  fullUrl: string;
  category: string | null;
  categoryId: number | null;
  isFeatured: boolean;
  sortOrder: number;
  width: number;
  height: number;
  cameraModel: string | null;
  lens: string | null;
  focalLength: string | null;
  aperture: string | null;
  shutterSpeed: string | null;
  iso: string | null;
  createdAt: string;
}
