import { IFerme } from 'app/shared/model/ferme.model';

export interface IAppUser {
  id?: number;
  username?: string | null;
  email?: string | null;
  password?: string | null;
  address?: string | null;
  phone?: string | null;
  role?: string | null;
  image?: string | null;
  fermes?: IFerme[] | null;
}

export const defaultValue: Readonly<IAppUser> = {};
