import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { siteApi } from '../services/api';
import type { Site } from '../services/api';

const ENTROPY_MODES = ['NONE', 'DAILY', 'USER_BASED', 'CRYPTOGRAPHIC'];

export default function SiteListPage() {
  const [sites, setSites] = useState<Site[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [newSiteName, setNewSiteName] = useState('');
  const [newEntropyMode, setNewEntropyMode] = useState('DAILY');
  const [newSanity, setNewSanity] = useState(50);

  useEffect(() => { loadSites(); }, []);

  const loadSites = async () => {
    try {
      setLoading(true);
      const response = await siteApi.getAll();
      setSites(response.data);
      setError(null);
    } catch {
      setError('Failed to connect to the void. Make sure the backend is running on port 8080.');
    } finally {
      setLoading(false);
    }
  };

  const createSite = async () => {
    if (!newSiteName.trim()) return;
    try {
      await siteApi.create(newSiteName, newEntropyMode, newSanity);
      setNewSiteName('');
      setShowCreateForm(false);
      loadSites();
    } catch {
      setError('Failed to create site');
    }
  };

  const deleteSite = async (id: string, name: string) => {
    if (window.confirm(`Consign "${name}" to oblivion? This cannot be undone.`)) {
      try {
        await siteApi.delete(id);
        loadSites();
      } catch {
        setError('Failed to delete site');
      }
    }
  };

  return (
    <div className="page">
      <div className="sites-header">
        <h2>Grimoires</h2>
        <button className="btn-primary" onClick={() => setShowCreateForm(!showCreateForm)}>
          {showCreateForm ? 'Cancel' : '+ Conjure New'}
        </button>
      </div>

      {error && (
        <div className="error-message">
          <span>⚠️ {error}</span>
          <button onClick={loadSites}>Retry</button>
        </div>
      )}

      {showCreateForm && (
        <div className="create-form">
          <input
            type="text"
            placeholder="Grimoire name..."
            value={newSiteName}
            onChange={(e) => setNewSiteName(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && createSite()}
          />
          <select value={newEntropyMode} onChange={(e) => setNewEntropyMode(e.target.value)}>
            {ENTROPY_MODES.map(m => <option key={m} value={m}>{m}</option>)}
          </select>
          <label className="sanity-label">
            Sanity: {newSanity}%
            <input type="range" min={0} max={100} value={newSanity} onChange={e => setNewSanity(Number(e.target.value))} />
          </label>
          <button onClick={createSite}>Awaken the Void</button>
        </div>
      )}

      {loading ? (
        <div className="loading">The void stirs...</div>
      ) : sites.length === 0 ? (
        <div className="empty-state">
          <p>No grimoires yet. Create one to begin your descent.</p>
        </div>
      ) : (
        <div className="sites-grid">
          {sites.map((site) => (
            <div key={site.id} className="site-card">
              <div className="site-card-header">
                <h3>{site.name}</h3>
                <button
                  className="delete-btn"
                  onClick={() => deleteSite(site.id, site.name)}
                  title="Consign to oblivion"
                >🕳️</button>
              </div>
              <div className="site-card-details">
                <p><strong>Entropy:</strong> {site.entropyMode}</p>
                <p><strong>Corruption:</strong> {site.corruptionIntensity}%</p>
                <p><strong>Entries:</strong> {site.entryCount}</p>
                <p><strong>Ward:</strong> <span className="ward">{site.entityWard}</span></p>
              </div>
              <Link to={`/site/${site.id}`} className="btn-view">
                Enter the Grimoire →
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
