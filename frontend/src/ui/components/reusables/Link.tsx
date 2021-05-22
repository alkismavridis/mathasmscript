import React, {useContext} from 'react';
import {RouterContext} from "../../utils/DiContext";

interface Props {
    href: string;
    className?: string;
    openInNewTab?: boolean;
    children: any;
}

export default function Link(props: Props) {
    const router = useContext(RouterContext);
    return <a
        className={props.className}
        onClick={e => handleClick(e)}
        target={props.openInNewTab? "_blank" : null}
        href={props.href}>{props.children}
    </a>;
    
    function handleClick(e: React.MouseEvent<HTMLAnchorElement>) {
        const isRelative = props.href.startsWith("/");
        if (!props.openInNewTab && isRelative) {
            e.preventDefault();
            router.route(props.href);
        }
    }
}
