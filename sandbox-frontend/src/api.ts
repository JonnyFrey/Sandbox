import axios, {AxiosInstance, AxiosResponse} from "axios";

/**
 * ES6 Axios Class.
 *
 * @class Api
 * @extends {Axios}
 * @example
 * class UserApi extends Api {
 *   public constructor (config) {
 *     super(config);
 *
 *     this.login=this.login.bind(this);
 *   }
 *
 *   public login (user: User) {
 *     return this.api.post<string, User, AxiosResponse<User>>("https://www.domain/login", {name: user.name, pass: user.pass})
 *        .then((res: AxiosResponse<string>) => res.data);
 *   }
 * }
 */

export const apiConfig = {
    returnRejectedPromiseOnError: true,
    withCredentials: false,
    timeout: 30000,
    baseURL: `http://${window.location.hostname}:9000`,
    headers: {
        common: {
            "Cache-Control": "no-cache, no-store, must-revalidate",
            Pragma: "no-cache",
            "Content-Type": "application/json",
            Accept: "application/json",
        },
    }
}

export interface Probability {
    x: number,
    y: number,
    chance: number
}

export class Api {
    private client: AxiosInstance;

    public constructor () {
        this.client = axios.create(apiConfig)
    }

    public getProbability(bombs: number, map: string[]): Promise<AxiosResponse<Probability[]>> {
        return this.client.post<Probability[]>('/minesweeper', {
            bombs: bombs,
            map: map
        })
    }

}

export const api = new Api();