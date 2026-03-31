import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { siteApi, entryApi, buildApi } from '../services/api';
import type { Site, Entry, BuildResult, BuildLog } from '../services/api';
import GooeyNav from '../components/GooeyNav';
import VoidModal from '../components/VoidModal';

const ENTROPY_MODES = ['NONE', 'DAILY', 'USER_BASED', 'CRYPTOGRAPHIC'];

export default function SiteDetailPage() {
  const { siteId } = useParams<{ siteId: string }>();
  const navigate = useNavigate();

  const [site, setSite] = useState<Site | null>(null);
  const [entries, setEntries] = useState<Entry[]>([]);
  const [builds, setBuilds] = useState<BuildLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Build state
  const [building, setBuilding] = useState(false);
  const [buildResult, setBuildResult] = useState<BuildResult | null>(null);

  // Settings editing
  const [editingSettings, setEditingSettings] = useState(false);
  const [entropyMode, setEntropyMode] = useState('');
  const [intensity, setIntensity] = useState(0);

  // Tab state
  const [activeTab, setActiveTab] = useState<'entries' | 'builds' | 'settings'>('entries');
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [generating, setGenerating] = useState(false);

  useEffect(() => {
    if (siteId) loadAll();
  }, [siteId]);

  const loadAll = async () => {
    try {
      setLoading(true);
      const [siteRes, entriesRes, buildsRes] = await Promise.all([
        siteApi.get(siteId!),
        entryApi.getBySite(siteId!),
        buildApi.getHistory(siteId!),
      ]);
      setSite(siteRes.data);
      setEntries(entriesRes.data);
      setBuilds(buildsRes.data);
      setEntropyMode(siteRes.data.entropyMode);
      setIntensity(siteRes.data.corruptionIntensity);
      setError(null);
    } catch {
      setError('Failed to load grimoire from the void.');
    } finally {
      setLoading(false);
    }
  };

  const triggerBuild = async () => {
    try {
      setBuilding(true);
      setBuildResult(null);
      const res = await buildApi.trigger(siteId!);
      setBuildResult(res.data);
      loadAll();
    } catch {
      setError('Build failed — the void rejected your offering.');
    } finally {
      setBuilding(false);
    }
  };

  const saveSettings = async () => {
    try {
      await siteApi.updateEntropy(siteId!, entropyMode);
      await siteApi.updateIntensity(siteId!, intensity);
      setEditingSettings(false);
      loadAll();
    } catch {
      setError('Failed to update settings.');
    }
  };

  const deleteSite = async () => {
    if (!site) return;
    setShowDeleteConfirm(true);
  };

  const executeDelete = async () => {
    try {
      await siteApi.delete(siteId!);
      navigate('/');
    } catch {
      setError('Failed to delete site.');
    } finally {
      setShowDeleteConfirm(false);
    }
  };

  const generateEntry = async () => {
    try {
      setGenerating(true);
      const res = await entryApi.generate(siteId!);
      loadAll();
      navigate(`/site/${siteId}/entries/${res.data.slug}`);
    } catch {
      setError('The void could not be channeled. Check your ANTHROPIC_API_KEY.');
    } finally {
      setGenerating(false);
    }
  };

  if (loading) return <div className="loading">Summoning the grimoire...</div>;
  if (!site) return <div className="error-message">Grimoire not found in the void.</div>;

  return (
    <div className="page">
      <div className="page-breadcrumb">
        <Link to="/">← All Grimoires</Link>
      </div>

      <div className="site-detail-header">
        <div>
          <h2>{site.name}</h2>
          <p className="subtitle">
            {site.entropyMode} · {site.corruptionIntensity}% corruption · {site.entryCount} entries
          </p>
        </div>
        <GooeyNav
          items={[
            { label: building ? '⏳ Building...' : '⚡ Trigger Build', onClick: building ? undefined : triggerBuild },
            { label: '🕳️ Delete', onClick: deleteSite },
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

      {buildResult && (
        <div className="build-result">
          <h3>{buildResult.success ? '✅ Build Complete' : '❌ Build Failed'}</h3>
          <div className="build-result-stats">
            <span>⏱️ {buildResult.durationMs}ms</span>
            <span>👁️ {buildResult.entityDetections} entities detected</span>
            <span>🌀 {buildResult.corruptedEntries} entries corrupted</span>
          </div>
          <p className="build-narrative">{buildResult.narrative}</p>
          <button className="btn-small" onClick={() => setBuildResult(null)}>Dismiss</button>
        </div>
      )}

      <GooeyNav
        items={[
          { label: `Entries (${entries.length})`, onClick: () => setActiveTab('entries') },
          { label: `Build History (${builds.length})`, onClick: () => setActiveTab('builds') },
          { label: 'Settings', onClick: () => setActiveTab('settings') },
        ]}
        initialActiveIndex={0}
        particleCount={15}
        particleDistances={[90, 10]}
        particleR={100}
        animationTime={600}
        timeVariance={300}
        colors={[1, 2, 3, 1, 2, 3, 1, 4]}
      />

      {activeTab === 'entries' && (
        <div className="tab-content">
          <div className="section-header">
            <h3>Entries</h3>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button
                className="btn-primary btn-small"
                onClick={generateEntry}
                disabled={generating}
                title="Let Claude channel a hallucinatory journal entry from the void"
              >
                {generating ? '🌀 Channeling...' : '👁️ Channel the Void'}
              </button>
              <Link to={`/site/${siteId}/entries/new`} className="btn-primary btn-small">+ New Entry</Link>
            </div>
          </div>
          {entries.length === 0 ? (
            <div className="empty-state">No entries yet. Write something into the void.</div>
          ) : (
            <div className="entries-list">
              {entries.map(entry => (
                <Link key={entry.id} to={`/site/${siteId}/entries/${entry.slug}`} className="entry-card">
                  <div className="entry-card-header">
                    <h4>{entry.title}</h4>
                    <div className="entry-meta">
                      {entry.requiresRitual && <span className="badge badge-ritual">🕯️ Ritual</span>}
                      {entry.entityInfluence && <span className="badge badge-entity">👁️ {entry.entityInfluence}</span>}
                      <span className="badge badge-corruption">🌀 {entry.corruptionLevel}%</span>
                    </div>
                  </div>
                  <p className="entry-preview">{entry.preview}</p>
                  <div className="entry-footer">
                    <span>/{entry.slug}</span>
                    <span>{entry.viewCount} views</span>
                    <span>{new Date(entry.createdAt).toLocaleDateString()}</span>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'builds' && (
        <div className="tab-content">
          <h3>Build History</h3>
          {builds.length === 0 ? (
            <div className="empty-state">No builds yet. Trigger one to awaken the corruption engine.</div>
          ) : (
            <div className="builds-list">
              {builds.map(build => (
                <div key={build.id} className={`build-card ${build.buildSuccessful ? '' : 'build-failed'}`}>
                  <div className="build-card-header">
                    <span>{build.buildSuccessful ? '✅' : '❌'} {new Date(build.timestamp).toLocaleString()}</span>
                    <span className="build-duration">⏱️ {build.buildDurationMs}ms</span>
                  </div>
                  <div className="build-stats">
                    <span>👁️ {build.entityDetections} entities</span>
                    <span>🌀 {build.corruptedEntries} corrupted</span>
                    <span>⚠️ {build.warningsCount} warnings</span>
                    <span>👻 {build.whispersGenerated} whispers</span>
                  </div>
                  {build.narrative && (
                    <p className="build-narrative">{build.narrative}</p>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'settings' && (
        <div className="tab-content">
          <h3>Site Settings</h3>
          <div className="settings-panel">
            <div className="setting-row">
              <label>Entropy Mode</label>
              {editingSettings ? (
                <select value={entropyMode} onChange={e => setEntropyMode(e.target.value)}>
                  {ENTROPY_MODES.map(m => <option key={m} value={m}>{m}</option>)}
                </select>
              ) : (
                <span>{site.entropyMode}</span>
              )}
            </div>
            <div className="setting-row">
              <label>Corruption Intensity</label>
              {editingSettings ? (
                <div className="range-input">
                  <input type="range" min={0} max={100} value={intensity} onChange={e => setIntensity(Number(e.target.value))} />
                  <span>{intensity}%</span>
                </div>
              ) : (
                <span>{site.corruptionIntensity}%</span>
              )}
            </div>
            <div className="setting-row">
              <label>Sanity Threshold</label>
              <span>{site.sanityThreshold}</span>
            </div>
            <div className="setting-row">
              <label>Entity Ward</label>
              <span className="ward">{site.entityWard}</span>
            </div>
            <div className="setting-row">
              <label>Last Built</label>
              <span>{site.lastBuilt ? new Date(site.lastBuilt).toLocaleString() : 'Never'}</span>
            </div>
            <div className="settings-actions">
              {editingSettings ? (
                <>
                  <button className="btn-primary btn-small" onClick={saveSettings}>Save</button>
                  <button className="btn-small" onClick={() => setEditingSettings(false)}>Cancel</button>
                </>
              ) : (
                <button className="btn-primary btn-small" onClick={() => setEditingSettings(true)}>Edit</button>
              )}
            </div>
          </div>
        </div>
      )}

      {showDeleteConfirm && site && (
        <VoidModal
          message={`Consign "${site.name}" to oblivion? This cannot be undone.`}
          confirmText="Consign to Oblivion"
          cancelText="Turn Back"
          onConfirm={executeDelete}
          onCancel={() => setShowDeleteConfirm(false)}
        />
      )}
    </div>
  );
}
