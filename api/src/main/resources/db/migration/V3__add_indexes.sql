CREATE INDEX IF NOT EXISTS idx_photos_category_id ON photos (category_id);
CREATE INDEX IF NOT EXISTS idx_photos_is_featured ON photos (is_featured);
CREATE INDEX IF NOT EXISTS idx_photos_sort_order ON photos (sort_order);
CREATE INDEX IF NOT EXISTS idx_categories_slug ON categories (slug);
