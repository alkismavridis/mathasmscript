export default class Router {
    constructor(
        public readonly route: (url: string) => void
    ) {}
}
