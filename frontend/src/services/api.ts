import axios from 'axios';

const API_BASE_URL = '/api';

export type Site = {
  id: string;
  name: string;
  entropyMode: string;
  sanityThreshold: number;
  corruptionIntensity: number;
  entryCount: number;
  lastBuilt: string | null;
  entityWard: string;
};

export type Entry = {
  id: string;
  title: string;
  slug: string;
  content: string;
  preview: string;
  corruptionLevel: number;
  entityInfluence: string | null;
  requiresRitual: boolean;
  viewCount: number;
  createdAt: string;
  lastCorrupted: string | null;
};

export type BuildResult = {
  narrative: string;
  buildLogId: string;
  entityDetections: number;
  corruptedEntries: number;
  durationMs: number;
  success: boolean;
};

export type BuildLog = {
  id: string;
  timestamp: string;
  narrative: string;
  entityDetections: number;
  corruptedEntries: number;
  buildDurationMs: number;
  buildSuccessful: boolean;
  warningsCount: number;
  whispersGenerated: number;
};

export type CorruptionPreview = {
  corruptedContent: string;
  corruptionPercentage: number;
  corruptedElements: string[];
  narrative: string;
};

const api = axios.create({
  baseURL: API_BASE_URL,
});

export const siteApi = {
  getAll: () => api.get<Site[]>('/sites'),
  get: (id: string) => api.get<Site>(`/sites/${id}`),
  create: (name: string, entropyMode: string, sanityThreshold: number) =>
    api.post<Site>('/sites', null, { params: { name, entropyMode, sanityThreshold } }),
  delete: (id: string) => api.delete(`/sites/${id}`),
  updateEntropy: (id: string, mode: string) =>
    api.patch<Site>(`/sites/${id}/entropy`, null, { params: { mode } }),
  updateIntensity: (id: string, intensity: number) =>
    api.patch<Site>(`/sites/${id}/intensity`, null, { params: { intensity } }),
};

export const entryApi = {
  getBySite: (siteId: string) => api.get<Entry[]>(`/sites/${siteId}/entries`),
  get: (siteId: string, slug: string) => api.get<Entry>(`/sites/${siteId}/entries/${slug}`),
  create: (siteId: string, title: string, slug: string, content: string) =>
    api.post<Entry>(`/sites/${siteId}/entries`, null, { params: { title, slug, content } }),
  update: (siteId: string, slug: string, content: string) =>
    api.put<Entry>(`/sites/${siteId}/entries/${slug}`, null, { params: { content } }),
  delete: (siteId: string, slug: string) =>
    api.delete(`/sites/${siteId}/entries/${slug}`),
  corrupt: (siteId: string, slug: string, viewerHash: string) =>
    api.get<CorruptionPreview>(`/sites/${siteId}/entries/${slug}/corrupt`, {
      headers: { 'X-Viewer-Hash': viewerHash }
    }),
};

export const buildApi = {
  trigger: (siteId: string) => api.post<BuildResult>(`/sites/${siteId}/builds`),
  getHistory: (siteId: string) => api.get<BuildLog[]>(`/sites/${siteId}/builds`),
};