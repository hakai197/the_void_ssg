import { useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { entryApi } from '../services/api';

export default function EntryCreatePage() {
  const { siteId } = useParams<{ siteId: string }>();
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [slug, setSlug] = useState('');
  const [content, setContent] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const autoSlug = (text: string) => {
    return text
      .toLowerCase()
      .replace(/[^a-z0-9\s-]/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-')
      .trim();
  };

  const handleTitleChange = (value: string) => {
    setTitle(value);
    if (!slug || slug === autoSlug(title)) {
      setSlug(autoSlug(value));
    }
  };

  const createEntry = async () => {
    if (!title.trim() || !slug.trim() || !content.trim()) {
      setError('Title, slug, and content are required.');
      return;
    }
    try {
      setSaving(true);
      await entryApi.create(siteId!, title, slug, content);
      navigate(`/site/${siteId}`);
    } catch {
      setError('Failed to create entry.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="page">
      <div className="page-breadcrumb">
        <Link to={`/site/${siteId}`}>← Back to Grimoire</Link>
      </div>

      <h2>New Entry</h2>

      {error && (
        <div className="error-message">
          <span>⚠️ {error}</span>
          <button onClick={() => setError(null)}>Dismiss</button>
        </div>
      )}

      <div className="entry-form">
        <div className="form-group">
          <label>Title</label>
          <input
            type="text"
            placeholder="Entry title..."
            value={title}
            onChange={(e) => handleTitleChange(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label>Slug</label>
          <input
            type="text"
            placeholder="entry-slug"
            value={slug}
            onChange={(e) => setSlug(e.target.value)}
          />
          <span className="form-hint">URL path: /{slug || '...'}</span>
        </div>
        <div className="form-group">
          <label>Content</label>
          <textarea
            placeholder="Write into the void..."
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={20}
          />
        </div>
        <div className="form-actions">
          <button className="btn-primary" onClick={createEntry} disabled={saving}>
            {saving ? '⏳ Creating...' : 'Inscribe into the Void'}
          </button>
          <Link to={`/site/${siteId}`} className="btn-small">Cancel</Link>
        </div>
      </div>
    </div>
  );
}
