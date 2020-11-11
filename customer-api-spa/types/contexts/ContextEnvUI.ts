import { LangItem } from '../LangItem'
import { WorkspaceUI } from './WorkspaceUI'

export interface ContextEnvUI {
  solverId: string;
  solverName: string;
  solverHash: string;
  email: string;
  preferredLang: LangItem;
  workspace: WorkspaceUI;
  workspaces: WorkspaceUI[];
  created: Date;
  updated: Date;
}