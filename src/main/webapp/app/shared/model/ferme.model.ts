import { IParcelle } from 'app/shared/model/parcelle.model';
import { IAppUser } from 'app/shared/model/app-user.model';

export interface IFerme {
  id?: number;
  libelle?: string | null;
  photo?: string | null;
  parcelles?: IParcelle[] | null;
  users?: IAppUser | null;
  appUser?: IAppUser | null;
}

export const defaultValue: Readonly<IFerme> = {};
