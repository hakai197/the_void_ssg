import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { entryApi } from '../services/api';
import type { Entry, CorruptionPreview } from '../services/api';
import GooeyNav from '../components/GooeyNav';
import VoidModal from '../components/VoidModal';

export default function EntryDetailPage() {
  const { siteId, slug } = useParams<{ siteId: string; slug: string }>();
  const navigate = useNavigate();

  const [entry, setEntry] = useState<Entry | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Editing
  const [editing, setEditing] = useState(false);
  const [editContent, setEditContent] = useState('');

  // Corruption preview
  const [corruption, setCorruption] = useState<CorruptionPreview | null>(null);
  const [showCorruption, setShowCorruption] = useState(false);
  const [corruptionLoading, setCorruptionLoading] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [channeling, setChanneling] = useState(false);

  useEffect(() => {
    if (siteId && slug) loadEntry();
  }, [siteId, slug]);

  const loadEntry = async () => {
    try {
      setLoading(true);
      const res = await entryApi.get(siteId!, slug!);
      setEntry(res.data);
      setEditContent(res.data.content);
      setError(null);
    } catch {
      setError('Failed to retrieve entry from the void.');
    } finally {
      setLoading(false);
    }
  };

  const saveEntry = async () => {
    try {
      await entryApi.update(siteId!, slug!, editContent);
      setEditing(false);
      loadEntry();
    } catch {
      setError('Failed to save entry.');
    }
  };

  const deleteEntry = async () => {
    if (!entry) return;
    setShowDeleteConfirm(true);
  };

  const executeDelete = async () => {
    try {
      await entryApi.delete(siteId!, slug!);
      navigate(`/site/${siteId}`);
    } catch {
      setError('Failed to delete entry.');
    } finally {
      setShowDeleteConfirm(false);
    }
  };

  const previewCorruption = async () => {
    try {
      setCorruptionLoading(true);
      const viewerHash = crypto.randomUUID();
      const res = await entryApi.corrupt(siteId!, slug!, viewerHash);
      setCorruption(res.data);
      setShowCorruption(true);
    } catch {
      setError('Corruption preview failed.');
    } finally {
      setCorruptionLoading(false);
    }
  };

  const channelEntry = async () => {
    try {
      setChanneling(true);
      setError(null);
      await entryApi.channel(siteId!, slug!);
      await loadEntry();
    } catch {
      setError('The void could not channel through this entry.');
    } finally {
      setChanneling(false);
    }
  };

  if (loading) return <div className="loading">Retrieving entry from the abyss...</div>;
  if (!entry) return <div className="error-message">Entry not found in the void.</div>;

  return (
    <div className="page">
      <div className="page-breadcrumb">
        <Link to={`/site/${siteId}`}>← Back to Grimoire</Link>
      </div>

      <div className="entry-detail-header">
        <div>
          <h2>{entry.title}</h2>
          <p className="subtitle">/{entry.slug} · {entry.viewCount} views · Created {new Date(entry.createdAt).toLocaleDateString()}</p>
        </div>
        <GooeyNav
          items={[
            {
              label: channeling ? '⏳ Channeling...' : '👁️ Channel the Void',
              onClick: channeling ? undefined : channelEntry,
            },
            {
              label: corruptionLoading ? '⏳ Corrupting...' : '🌀 Preview Corruption',
              onClick: corruptionLoading ? undefined : previewCorruption,
            },
            { label: editing ? 'Cancel Edit' : '✏️ Edit', onClick: () => setEditing(!editing) },
            { label: '🗑️ Delete', onClick: deleteEntry },
          ]}
          initialActiveIndex={0}
          particleCount={12}
          particleDistances={[90, 10]}
          particleR={80}
          animationTime={500}
          timeVariance={200}
          colors={[1, 2, 3, 1, 2, 3, 1, 4]}
        />
      </div>

      {error && (
        <div className="error-message">
          <span>⚠️ {error}</span>
          <button onClick={() => setError(null)}>Dismiss</button>
        </div>
      )}

      <div className="entry-badges">
        {entry.requiresRitual && <span className="badge badge-ritual">🕯️ Requires Ritual</span>}
        {entry.entityInfluence && <span className="badge badge-entity">👁️ {entry.entityInfluence}</span>}
        <span className="badge badge-corruption">🌀 Corruption: {entry.corruptionLevel}%</span>
        {entry.lastCorrupted && (
          <span className="badge">Last corrupted: {new Date(entry.lastCorrupted).toLocaleDateString()}</span>
        )}
      </div>

      {editing ? (
        <div className="entry-editor">
          <textarea
            value={editContent}
            onChange={(e) => setEditContent(e.target.value)}
            rows={20}
            placeholder="Write into the void..."
          />
          <div className="editor-actions">
            <button className="btn-primary" onClick={saveEntry}>Save Changes</button>
            <button onClick={() => setEditing(false)}>Cancel</button>
          </div>
        </div>
      ) : (
        <div className="entry-content">
          <pre>{entry.content}</pre>
        </div>
      )}

      {showCorruption && corruption && (
        <div className="corruption-overlay">
          <div className="corruption-panel">
            <div className="corruption-header">
              <h3>🌀 Corruption Preview</h3>
              <button className="btn-small" onClick={() => setShowCorruption(false)}>✕</button>
            </div>
            <div className="corruption-stats">
              <span>Corruption: {corruption.corruptionPercentage}%</span>
              <span>Elements affected: {corruption.corruptedElements.length}</span>
            </div>
            <div className="corruption-narrative">
              <p><em>{corruption.narrative}</em></p>
            </div>
            <div className="corruption-content">
              <pre>{corruption.corruptedContent}</pre>
            </div>
            {corruption.corruptedElements.length > 0 && (
              <div className="corruption-elements">
                <h4>Corrupted Elements:</h4>
                <ul>
                  {corruption.corruptedElements.map((el, i) => (
                    <li key={i}>{el}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        </div>
      )}

      {showDeleteConfirm && entry && (
        <VoidModal
          message={`Delete "${entry.title}"? This cannot be undone.`}
          confirmText="Delete Entry"
          cancelText="Turn Back"
          onConfirm={executeDelete}
          onCancel={() => setShowDeleteConfirm(false)}
        />
      )}
    </div>
  );
}
